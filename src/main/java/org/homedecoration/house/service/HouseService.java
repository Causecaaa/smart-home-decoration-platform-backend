package org.homedecoration.house.service;

import org.homedecoration.bill.entity.Bill;
import org.homedecoration.bill.repository.BillRepository;
import org.homedecoration.house.dto.request.CreateHouseRequest;
import org.homedecoration.house.dto.request.UpdateHouseRequest;
import org.homedecoration.house.entity.House;
import org.homedecoration.house.repository.HouseRepository;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.identity.user.repository.UserRepository;
import org.homedecoration.layout.entity.HouseLayout;
import org.homedecoration.layout.repository.HouseLayoutRepository;
import org.homedecoration.stage.stage.service.StageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseService {

    private final HouseRepository houseRepository;
    private final UserRepository userRepository;
    private final HouseLayoutRepository layoutRepository;
    private final BillRepository billRepository;
    private final StageService stageService;

    public HouseService(
            HouseRepository houseRepository,
            UserRepository userRepository,
            HouseLayoutRepository layoutRepository,
            BillRepository billRepository,
            StageService stageService) {
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
        this.layoutRepository = layoutRepository;
        this.billRepository = billRepository;
        this.stageService = stageService;
    }

    // ================== 创建 ==================

    public House createHouse(CreateHouseRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        House house = new House();
        house.setUser(user);
        house.setCity(request.getCity());
        house.setCommunityName(request.getCommunityName());
        house.setBuildingNo(request.getBuildingNo());
        house.setUnitNo(request.getUnitNo());
        house.setRoomNo(request.getRoomNo());
        house.setArea(request.getArea());
        house.setLayoutType(request.getLayoutType());
        house.setDecorationType(request.getDecorationType());
        house.setFloorCount(request.getFloorCount());

        House savedHouse = houseRepository.save(house);

        if(House.DecorationType.LOOSE.equals(savedHouse.getDecorationType())){
            stageService.createStagesAsync(savedHouse.getId());
        }

        return savedHouse;
    }

    // ================== 查询 ==================

    public List<House> getAllHousesByUserId(Long userId) {
        return houseRepository.findAllByUserId(userId).stream()
                .map(this::calculateHouseFields)
                .toList();
    }

    public House getHouseById(Long id) {
        return houseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("House not found"));
    }

    public House getHouseWithCalculatedFields(Long id) {
        House house = getHouseById(id);
        return calculateHouseFields(house);
    }

    // ================== 计算房屋相关字段 ==================
    private House calculateHouseFields(House house) {
        house.setCanStartQuotation(canStartQuotation(house.getConfirmedLayoutId()));
        house.setCanStartConstruction(canStartConstruction(house.getId()));
        return house;
    }

    // ================== 是否可施工（核心） ==================

    public boolean canStartQuotation(Long confirmedLayoutId) {
        if (confirmedLayoutId == null) {
            return false;
        }
        return billRepository.existsByBizTypeAndBizIdAndPayStatus(
                Bill.BizType.FURNITURE,
                confirmedLayoutId,
                Bill.PayStatus.PAID
        );
    }

    public boolean canStartConstruction(Long houseId) {
        if (houseId == null) {
            return false;
        }
        return billRepository.existsByBizTypeAndBizIdAndPayStatus(
                Bill.BizType.CONSTRUCTION,
                houseId,
                Bill.PayStatus.PAID
        );
    }


    // ================== 更新 / 删除前校验 ==================

    public void validCheck(Long houseId, Long userId) {
        House house = getHouseById(houseId);

        if (!house.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权限操作该房源");
        }

        List<HouseLayout> layouts = layoutRepository.findAllByHouseId(houseId);
        for (HouseLayout layout : layouts) {
            if (layout.getLayoutStatus() != HouseLayout.LayoutStatus.DRAFT) {
                throw new RuntimeException("户型已发布，无法修改或删除");
            }
        }
    }

    // ================== 更新 ==================

    public House updateHouse(UpdateHouseRequest request, Long houseId, Long userId) {
        validCheck(houseId, userId);

        House existing = getHouseById(houseId);
        existing.setCity(request.getCity());
        existing.setCommunityName(request.getCommunityName());
        existing.setBuildingNo(request.getBuildingNo());
        existing.setUnitNo(request.getUnitNo());
        existing.setRoomNo(request.getRoomNo());
        existing.setArea(request.getArea());
        existing.setLayoutType(request.getLayoutType());
        existing.setFloorCount(request.getFloorCount());

        return calculateHouseFields(houseRepository.save(existing));
    }

    // ================== 删除 ==================

    public void deleteHouse(Long houseId, Long userId) {
        validCheck(houseId, userId);
        houseRepository.deleteById(houseId);
    }
}
