package org.homedecoration.house.service;

import lombok.RequiredArgsConstructor;
import org.homedecoration.bill.dto.request.CreateBillRequest;
import org.homedecoration.bill.entity.Bill;
import org.homedecoration.bill.repository.BillRepository;
import org.homedecoration.bill.service.BillService;
import org.homedecoration.furniture.SchemeRoomMaterial.entity.SchemeRoomMaterial;
import org.homedecoration.furniture.SchemeRoomMaterial.service.SchemeRoomMaterialService;
import org.homedecoration.house.dto.response.HouseMaterialSummaryResponse;
import org.homedecoration.house.dto.response.HouseQuotationResponse;
import org.homedecoration.house.entity.House;
import org.homedecoration.houseRoom.entity.HouseRoom;
import org.homedecoration.houseRoom.service.HouseRoomService;
import org.homedecoration.material.auxiliary.AuxiliaryMaterial;
import org.homedecoration.material.auxiliary.AuxiliaryMaterialRepository;
import org.homedecoration.material.material.Material;
import org.homedecoration.material.material.MaterialRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HouseQuotationService {

    private final MaterialRepository materialRepository;
    private final AuxiliaryMaterialRepository auxiliaryMaterialRepository;
    private final HouseRoomService houseRoomService;
    private final SchemeRoomMaterialService schemeRoomMaterialService;
    private final HouseService houseService;
    private final BillService billService;
    private final BillRepository billRepository;

    // 人工单价（元/㎡），可配置或从数据库读取
    private final BigDecimal LABOR_UNIT_PRICE = new BigDecimal("100");


    /**
     * 计算房屋报价（主材 + 辅材 + 人工费）
     */
    public HouseQuotationResponse calculateHouseQuotation(Long houseId, Long userId) {
        if(!houseService.getHouseById(houseId).getUser().getId().equals(userId)){
            throw new RuntimeException("无权限访问");
        }

        HouseQuotationResponse response = new HouseQuotationResponse();

        // 1️⃣ 获取房间列表
        List<HouseRoom> rooms = houseRoomService.getConfirmedRoomsByHouseId(houseId);
        List<HouseQuotationResponse.RoomQuotation> roomQuotations = new ArrayList<>();
        BigDecimal mainMaterialTotal = BigDecimal.ZERO;
        BigDecimal totalArea = BigDecimal.ZERO; // 用于人工费计算

        for (HouseRoom room : rooms) {
            HouseQuotationResponse.RoomQuotation roomQuotation = new HouseQuotationResponse.RoomQuotation();
            roomQuotation.setRoomId(room.getId());
            roomQuotation.setRoomName(room.getRoomName());

            // 获取房间主材选材信息
            SchemeRoomMaterial scheme = schemeRoomMaterialService.getByRoomId(room.getId());
            HouseQuotationResponse.MainMaterials mainMaterials = new HouseQuotationResponse.MainMaterials();

            mainMaterials.setFloor(buildMaterialDetail(
                    String.valueOf(scheme.getFloorMaterial()),
                    scheme.getFloorArea(),
                    scheme.getFloor_notes() // 这里是你想显示的品牌 + 尺寸备注
            ));
            mainMaterials.setWall(buildMaterialDetail(
                    String.valueOf(scheme.getWallMaterial()),
                    scheme.getWallArea(),
                    scheme.getWall_notes()
            ));
            mainMaterials.setCeiling(buildMaterialDetail(
                    String.valueOf(scheme.getCeilingMaterial()),
                    scheme.getCeilingArea(),
                    scheme.getCeiling_notes()
            ));
            mainMaterials.setCabinet(buildMaterialDetail(
                    String.valueOf(scheme.getCabinetMaterial()),
                    scheme.getCabinetArea(),
                    scheme.getCabinet_notes()
            ));

            // 房间主材总价
            BigDecimal roomTotal = BigDecimal.ZERO;
            BigDecimal roomAreaSum = BigDecimal.ZERO;
            if (mainMaterials.getFloor() != null) {
                roomTotal = roomTotal.add(mainMaterials.getFloor().getCost());
                roomAreaSum = roomAreaSum.add(mainMaterials.getFloor().getArea());
            }
            if (mainMaterials.getWall() != null) {
                roomTotal = roomTotal.add(mainMaterials.getWall().getCost());
                roomAreaSum = roomAreaSum.add(mainMaterials.getWall().getArea());
            }
            if (mainMaterials.getCeiling() != null) {
                roomTotal = roomTotal.add(mainMaterials.getCeiling().getCost());
                roomAreaSum = roomAreaSum.add(mainMaterials.getCeiling().getArea());
            }
            if (mainMaterials.getCabinet() != null) {
                roomTotal = roomTotal.add(mainMaterials.getCabinet().getCost());
                roomAreaSum = roomAreaSum.add(mainMaterials.getCabinet().getArea());
            }

            roomQuotation.setTotalCost(roomTotal); // 设置房间总费用
            roomQuotation.setMainMaterials(mainMaterials);

            mainMaterialTotal = mainMaterialTotal.add(roomTotal);
            totalArea = totalArea.add(roomAreaSum);
            roomQuotations.add(roomQuotation);
        }

        response.setRooms(roomQuotations);

        // 2️⃣ 计算辅材明细和总价
        List<AuxiliaryMaterial> auxiliaries = auxiliaryMaterialRepository.findAll();
        List<HouseQuotationResponse.AuxiliaryMaterialItem> auxiliaryItems = new ArrayList<>();
        BigDecimal auxiliaryTotal = BigDecimal.ZERO;

        for (AuxiliaryMaterial aux : auxiliaries) {
            BigDecimal quantity = calculateAuxiliaryQuantity(aux, houseId);
            BigDecimal itemCost = aux.getUnitPrice().multiply(quantity);

            HouseQuotationResponse.AuxiliaryMaterialItem item = new HouseQuotationResponse.AuxiliaryMaterialItem();
            item.setName(aux.getName());
            item.setCategory(aux.getCategory());
            item.setUnit(aux.getUnit());
            item.setUnitPrice(aux.getUnitPrice());
            item.setQuantity(quantity);
            item.setCost(itemCost);
            item.setRemark(aux.getRemark());

            auxiliaryItems.add(item);
            auxiliaryTotal = auxiliaryTotal.add(itemCost);
        }

        response.setAuxiliaryMaterials(auxiliaryItems);
        response.setAuxiliaryMaterialsCost(auxiliaryTotal);

        // 3️⃣ 计算人工费
        BigDecimal laborCost = totalArea.multiply(LABOR_UNIT_PRICE);

        if(houseService.getHouseById(houseId).getDecorationType() == House.DecorationType.HALF){
            mainMaterialTotal = BigDecimal.valueOf(0);
        }
        response.setHouseId(houseId);
        response.setDecorationType(houseService.getHouseById(houseId).getDecorationType());
        response.setMainMaterialsCost(mainMaterialTotal);

        response.setLaborCost(laborCost);

        BigDecimal totalCost = mainMaterialTotal.add(auxiliaryTotal).add(laborCost);
        // 4️⃣ 总价 = 主材总价 + 辅材总价 + 人工费
        response.setTotalCost(totalCost);

        boolean exists = billRepository.existsByBizTypeAndBizId(
                Bill.BizType.CONSTRUCTION,
                houseId
        );
        if(! exists){
            Bill bill = generateQuotationBill(houseId, userId, totalCost);
            response.setBillId(bill.getId());
            response.setPayStatus(Bill.PayStatus.UNPAID);
        }else {
            Bill bill = billService.getBill(Bill.BizType.CONSTRUCTION, houseId);
            response.setBillId(bill.getId());
            response.setPayStatus(bill.getPayStatus());
        }

        // 5️⃣ 返回
        return response;
    }


    public HouseMaterialSummaryResponse calculateHouseMaterials(Long houseId) {
        HouseMaterialSummaryResponse response = new HouseMaterialSummaryResponse();
        response.setHouseId(houseId);

        List<HouseRoom> rooms = houseRoomService.getConfirmedRoomsByHouseId(houseId);

        // 初始化主材类别
        String[] mainTypes = {"FLOOR", "WALL", "CEILING", "CABINET"};
        for (String type : mainTypes) {
            response.getMainMaterials().put(type, new ArrayList<>());
        }

        // 汇总主材
        for (HouseRoom room : rooms) {
            // 获取房间材料方案并进行空值检查
            SchemeRoomMaterial scheme = schemeRoomMaterialService.getByRoomId(room.getId());
            if (scheme == null) {
                continue; // 跳过当前房间
            }

            Map<String, BigDecimal> roomTypeAreas = new HashMap<>();
            roomTypeAreas.put("FLOOR", scheme.getFloorArea() != null ? scheme.getFloorArea() : BigDecimal.ZERO);
            roomTypeAreas.put("WALL", scheme.getWallArea() != null ? scheme.getWallArea() : BigDecimal.ZERO);
            roomTypeAreas.put("CEILING", scheme.getCeilingArea() != null ? scheme.getCeilingArea() : BigDecimal.ZERO);
            roomTypeAreas.put("CABINET", scheme.getCabinetArea() != null ? scheme.getCabinetArea() : BigDecimal.ZERO);

            Map<String, Object> roomTypeMaterials = new HashMap<>();
            roomTypeMaterials.put("FLOOR", scheme.getFloorMaterial());
            roomTypeMaterials.put("WALL", scheme.getWallMaterial());
            roomTypeMaterials.put("CEILING", scheme.getCeilingMaterial());
            roomTypeMaterials.put("CABINET", scheme.getCabinetMaterial());

            for (String type : mainTypes) {
                Object mat = roomTypeMaterials.get(type);
                BigDecimal area = roomTypeAreas.get(type);

                // 添加安全检查 - 检查材料类型是否存在且面积大于0
                if (mat == null || area == null || area.compareTo(BigDecimal.ZERO) <= 0) {
                    continue; // 跳过当前材料类型
                }

                String materialType = String.valueOf(mat);
                List<Material> materialList = materialRepository.findByType(materialType);

                HouseMaterialSummaryResponse.MaterialDetail detail = new HouseMaterialSummaryResponse.MaterialDetail();
                detail.setType(type);

                if (!materialList.isEmpty()) {
                    detail.setDisplayName(materialList.get(0).getDisplayName());
                } else {
                    detail.setDisplayName(materialType);
                }

                detail.setArea(area);
                response.getMainMaterials().get(type).add(detail);
            }
        }

        // 汇总辅材
        List<AuxiliaryMaterial> auxiliaries = auxiliaryMaterialRepository.findAll();

        for (AuxiliaryMaterial aux : auxiliaries) {
            try {
                BigDecimal quantity = calculateAuxiliaryQuantity(aux, houseId);

                HouseMaterialSummaryResponse.AuxiliaryMaterialItem item = new HouseMaterialSummaryResponse.AuxiliaryMaterialItem();
                item.setName(aux.getName());
                item.setCategory(aux.getCategory());
                item.setUnit(aux.getUnit());
                item.setQuantity(quantity);
                item.setRemark(aux.getRemark());
                response.getAuxiliaryMaterials().add(item);
            } catch (Exception e) {
                // 记录错误但不影响整体流程
            }
        }

        return response;
    }





    /**
     * 生成报价账单
     */
    private Bill generateQuotationBill(Long houseId, Long userId, BigDecimal totalCost) {
        CreateBillRequest request = new CreateBillRequest();
        request.setBizType(Bill.BizType.CONSTRUCTION);  // 报价业务类型
        request.setBizId(houseId);                   // 房屋ID
        request.setAmount(totalCost);                // 总金额
        request.setDepositAmount(BigDecimal.ZERO);   // 报价账单无定金
        request.setRemark("房屋装修报价账单");

        return billService.createBill(request, userId); // 用户既是支付方也是收款方（临时）
    }

    /**
     * 构建主材明细
     */
    private HouseQuotationResponse.MaterialDetail buildMaterialDetail(String type, BigDecimal area, String remark) {
        if (type == null || area == null) {
            return null;
        }

        List<Material> materialList = materialRepository.findByType(type);
        Material material = materialList.isEmpty() ? null : materialList.get(0);

        HouseQuotationResponse.MaterialDetail detail = new HouseQuotationResponse.MaterialDetail();
        detail.setType(type);
        detail.setDisplayName(material != null ? material.getDisplayName() : type);
        detail.setArea(area);
        detail.setUnitPrice(material != null ? material.getUnitPrice() : BigDecimal.ZERO);
        detail.setCost(detail.getUnitPrice().multiply(area));
        detail.setRemark(remark); // 来自 SchemeRoomMaterial 的 floor_notes / wall_notes / ceiling_notes / cabinet_notes

        return detail;
    }


    /**
     * 计算辅材用量（可根据业务逻辑调整）
     */
    private BigDecimal calculateAuxiliaryQuantity(AuxiliaryMaterial auxiliary, Long houseId) {
        BigDecimal baseArea = houseService.getHouseWithCalculatedFields(houseId).getArea();
        int roomCount = houseRoomService.getConfirmedRoomsByHouseId(houseId).size();

        return switch (auxiliary.getCategory().toUpperCase()) {
            case "CEMENT" -> baseArea.multiply(new BigDecimal("0.1"));        // 水泥按面积比例
            case "PIPE" -> new BigDecimal(roomCount * 2);                     // 每房间2个管道点
            case "WIRE" -> baseArea.multiply(new BigDecimal("1.2"));          // 电线按面积比例
            case "FIXING_MATERIALS" -> new BigDecimal(roomCount * 5);         // 每房间固定材料5个
            case "ETC" -> calculateEtcMaterialQuantity(auxiliary, baseArea, roomCount);
            default -> BigDecimal.ONE;
        };
    }

    /**
     * ETC 类别辅材用量
     */
    private BigDecimal calculateEtcMaterialQuantity(AuxiliaryMaterial auxiliary, BigDecimal baseArea, int roomCount) {
        String name = auxiliary.getName().toLowerCase();

        return switch (name) {
            case "螺丝", "螺钉", "膨胀螺栓" -> baseArea.multiply(new BigDecimal("10"));   // 每平米10个
            case "胶水", "玻璃胶", "密封胶" -> baseArea.multiply(new BigDecimal("0.1")); // 每平米0.1kg
            case "保温棉", "隔音棉" -> baseArea.multiply(new BigDecimal("0.5"));         // 每平米0.5单位
            case "砂纸", "打磨纸" -> new BigDecimal(roomCount * 3);                     // 每房间3张
            case "保护膜", "防尘膜" -> baseArea.multiply(new BigDecimal("0.8"));         // 按面积比例
            default -> BigDecimal.ONE;
        };
    }
}
