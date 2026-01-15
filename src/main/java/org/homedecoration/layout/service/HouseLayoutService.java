package org.homedecoration.layout.service;

import jakarta.transaction.Transactional;
import org.homedecoration.bill.dto.request.CreateBillRequest;
import org.homedecoration.bill.dto.response.BillResponse;
import org.homedecoration.bill.entity.Bill;
import org.homedecoration.bill.repository.BillRepository;
import org.homedecoration.bill.service.BillService;
import org.homedecoration.common.exception.BusinessException;
import org.homedecoration.common.utils.LayoutPermissionUtil;
import org.homedecoration.house.entity.House;
import org.homedecoration.house.repository.HouseRepository;
import org.homedecoration.house.service.HouseService;
import org.homedecoration.identity.designer.entity.Designer;
import org.homedecoration.identity.designer.repository.DesignerRepository;
import org.homedecoration.identity.designer.service.DesignerService;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.identity.user.repository.UserRepository;
import org.homedecoration.identity.user.service.UserService;
import org.homedecoration.layout.dto.request.CreateLayoutRequest;
import org.homedecoration.layout.dto.request.UpdateLayoutRequest;
import org.homedecoration.layout.dto.response.CurrentLayoutResponse;
import org.homedecoration.layout.dto.response.LayoutHistoryItemResponse;
import org.homedecoration.layout.dto.response.LayoutOverviewResponse;
import org.homedecoration.layout.entity.HouseLayout;
import org.homedecoration.layout.repository.HouseLayoutRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class HouseLayoutService {

    private final HouseLayoutRepository houseLayoutRepository;
    private final HouseService houseService;
    private final UserService userService;
    private final LayoutPermissionUtil layoutPermissionUtil;
    private final DesignerRepository designerRepository;
    private final BillRepository billRepository;
    private final BillService billService;
    private final UserRepository userRepository;
    private final DesignerService designerService;

    public HouseLayoutService(HouseLayoutRepository houseLayoutRepository, HouseRepository houseRepository,
                              HouseService houseService, UserService userService,
                              LayoutPermissionUtil layoutPermissionUtil, DesignerRepository designerRepository,
                              BillRepository billRepository, BillService billService, UserRepository userRepository, DesignerService designerService) {
        this.houseLayoutRepository = houseLayoutRepository;
        this.houseService = houseService;
        this.userService = userService;
        this.layoutPermissionUtil = layoutPermissionUtil;
        this.designerRepository = designerRepository;
        this.billRepository = billRepository;
        this.billService = billService;
        this.userRepository = userRepository;
        this.designerService = designerService;
    }

    public CurrentLayoutResponse createDraft(CreateLayoutRequest request, Long userId) {
        House house = houseService.getHouseById(request.getHouseId());
        HouseLayout layout = new HouseLayout();
        layout.setHouse(house);
        layout.setLayoutIntent(request.getLayoutIntent());
        layout.setDesignerId(request.getDesignerId());
        layout.setRedesignNotes(request.getRedesignNotes());
        layout.setLayoutStatus(HouseLayout.LayoutStatus.DRAFT);
        layout.setLayoutVersion(0);

        HouseLayout savedLayout = houseLayoutRepository.save(layout);

        CreateBillRequest billRequest = new CreateBillRequest();
        billRequest.setBizType(Bill.BizType.LAYOUT);
        billRequest.setBizId(savedLayout.getId());
        billRequest.setAmount(new BigDecimal("5000"));
        billRequest.setDepositAmount(new BigDecimal("1000"));
        billRequest.setRemark("户型重设计服务定金");

        Bill bill = billService.createBill(billRequest, userId);

        User designer = designerService.getByDesignerId(request.getDesignerId()).getUser();

        return CurrentLayoutResponse.toDTO(savedLayout,bill,designer);
    }


    public HouseLayout createLayout(CreateLayoutRequest request) {
        House house = houseService.getHouseById(request.getHouseId());
        User user = userService.getById(request.getUserId());

        HouseLayout layout = new HouseLayout();
        layout.setHouse(house);
        layout.setLayoutIntent(request.getLayoutIntent());

        if (user.getRole() == User.Role.USER) {
            layout.setLayoutVersion(1);
            layout.setLayoutStatus(HouseLayout.LayoutStatus.SUBMITTED);
        }

        if (user.getRole() == User.Role.DESIGNER) {

            int nextVersion = houseLayoutRepository
                    .findTopByHouseIdOrderByLayoutVersionDesc(house.getId())
                    .map(HouseLayout::getLayoutVersion)
                    .orElse(0) + 1;

            layout.setLayoutVersion(nextVersion);
            layout.setDesignerId(user.getId());
            layout.setLayoutStatus(HouseLayout.LayoutStatus.SUBMITTED);
        }

        return houseLayoutRepository.save(layout);
    }

    public List<HouseLayout> getLayoutsByHouseId(Long houseId) {
        houseService.getHouseById(houseId);
        return houseLayoutRepository.findByHouseIdOrderByLayoutVersionAsc(houseId);
    }

    public HouseLayout getLayoutById(Long layoutId) {
        return houseLayoutRepository.findById(layoutId)
                .orElseThrow(() -> new RuntimeException("Layout not found with id: " + layoutId));
    }

    @Transactional
    public HouseLayout updateLayout(Long layoutId, UpdateLayoutRequest request, Long userId) {
        HouseLayout layout = getLayoutById(layoutId);
        User operator = userService.getById(userId);

        layoutPermissionUtil.checkCanEdit(operator, layout, userId);

        layout.setLayoutIntent(request.getLayoutIntent());
        layout.setRedesignNotes(request.getRedesignNotes());

        return houseLayoutRepository.save(layout);
    }


    @Transactional
    public void deleteLayout(Long layoutId, Long userId) {
        HouseLayout layout = getLayoutById(layoutId);   // 获取布局
        User operator = userService.getById(userId);   // 获取操作用户

        layoutPermissionUtil.checkCanEdit(operator, layout, userId);

        houseLayoutRepository.delete(layout);
    }


    @Transactional
    public HouseLayout confirmLayout(Long layoutId, Long userId) {
        HouseLayout layout = getLayoutById(layoutId);

        // 权限检查
        if (!layout.getHouse().getUser().getId().equals(userId)) {
            throw new RuntimeException("No permission to confirm this layout");
        }

        // 检查是否已有已确认的 layout
        Optional<HouseLayout> existingConfirmed = houseLayoutRepository
                .findTopByHouseIdAndLayoutStatus(layout.getHouse().getId(), HouseLayout.LayoutStatus.CONFIRMED);

        if (existingConfirmed.isPresent()) {
            throw new RuntimeException("This house already has a confirmed layout");
        }

        // 将其他 layout 标记为 ARCHIVED
        List<HouseLayout> otherLayouts = houseLayoutRepository
                .findByHouseIdAndIdNot(layout.getHouse().getId(), layoutId);

        for (HouseLayout other : otherLayouts) {
            other.setLayoutStatus(HouseLayout.LayoutStatus.ARCHIVED);
        }
        houseLayoutRepository.saveAll(otherLayouts);

        // 确认当前 layout
        layout.setLayoutStatus(HouseLayout.LayoutStatus.CONFIRMED);
        return houseLayoutRepository.save(layout);
    }

    @Transactional
    public HouseLayout confirmFurnitureDesigner(Long layoutId, Long furnitureDesignerId, Long userId) {
        HouseLayout layout = houseLayoutRepository.findById(layoutId)
                .orElseThrow(() -> new BusinessException("Layout不存在"));

        // 可以加权限校验：只有房主可以选择家具设计师
        if (!layout.getHouse().getUser().getId().equals(userId)) {
            throw new BusinessException("无权操作该布局");
        }

        // 只能在 CONFIRMED 状态后选择家具设计师
        if (layout.getLayoutStatus() != HouseLayout.LayoutStatus.CONFIRMED) {
            throw new BusinessException("请先确认布局方案");
        }

        if (!designerRepository.existsById(furnitureDesignerId)) {
            throw new BusinessException("选择的家具设计师不存在");
        }

        layout.setFurnitureDesignerId(furnitureDesignerId);
        HouseLayout savedLayout = houseLayoutRepository.save(layout);

        boolean billExists = billRepository.existsByBizTypeAndBizId(
                Bill.BizType.FURNITURE,
                layoutId
        );
        if (!billExists) {
            // 计算金额（你可以封装成单独方法）
            BigDecimal totalAmount = calculateFurnitureTotal(layout);
            BigDecimal depositAmount = calculateFurnitureDeposit(layout);

            Bill furnitureBill = new Bill();
            furnitureBill.setBizType(Bill.BizType.FURNITURE);
            furnitureBill.setBizId(layoutId);
            furnitureBill.setAmount(totalAmount);
            furnitureBill.setDepositAmount(depositAmount);
            furnitureBill.setPayStatus(Bill.PayStatus.UNPAID);
            furnitureBill.setPayerId(layout.getHouse().getUser().getId());
            furnitureBill.setPayeeId(furnitureDesignerId);
            furnitureBill.setRemark("家具阶段账单");

            billRepository.save(furnitureBill);
        }

        return savedLayout;
    }

    public BigDecimal calculateFurnitureTotal(HouseLayout layout) {
        if (layout == null || layout.getHouse() == null) {
            throw new BusinessException("房屋信息缺失，无法计算金额");
        }

        // 示例逻辑：按房屋面积计算，每平米 200 元
        BigDecimal area = layout.getHouse().getArea(); // 假设 House 有 getArea()
        BigDecimal pricePerSquare = new BigDecimal("200"); // 每平米价格，可调整
        return area.multiply(pricePerSquare);
    }

    public BigDecimal calculateFurnitureDeposit(HouseLayout layout) {
        BigDecimal total = calculateFurnitureTotal(layout);

        // 示例逻辑：定金为总金额的 30%
        BigDecimal depositRate = new BigDecimal("0.3");
        return total.multiply(depositRate).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Transactional()
    public LayoutOverviewResponse getLayoutOverview(Long houseId, Long userId) {

        LayoutOverviewResponse resp = new LayoutOverviewResponse();

        // 1️⃣ 当前 layout（version = 0）
        HouseLayout current = (HouseLayout) houseLayoutRepository
                .findByHouseIdAndLayoutVersion(houseId, 0)
                .orElse(null);

        if (current != null) {
            Bill bill = billRepository
                    .findByBizTypeAndBizId(Bill.BizType.LAYOUT, current.getId())
                    .orElse(null);

            userRepository
                    .findById(current.getDesignerId()).ifPresent(designer -> resp.setCurrentLayout(
                            CurrentLayoutResponse.toDTO(current, bill, designer)
                    ));

        }

        // 2️⃣ 历史 layouts（version > 0）
        List<LayoutHistoryItemResponse> history = houseLayoutRepository
                .findByHouseIdAndLayoutVersionGreaterThan(houseId, 0)
                .stream()
                .map(layout -> LayoutHistoryItemResponse.toDTO((HouseLayout) layout))
                .toList();

        resp.setHistoryLayouts(history);

        return resp;
    }

}
