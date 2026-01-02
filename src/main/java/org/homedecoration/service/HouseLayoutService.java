package org.homedecoration.service;

import jakarta.transaction.Transactional;
import org.homedecoration.dto.request.CreateLayoutRequest;
import org.homedecoration.dto.request.UpdateLayoutRequest;
import org.homedecoration.entity.House;
import org.homedecoration.entity.HouseLayout;
import org.homedecoration.entity.User;
import org.homedecoration.repository.HouseLayoutRepository;
import org.homedecoration.repository.HouseRepository;
import org.homedecoration.utils.LayoutPermissionUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HouseLayoutService {

    private final HouseLayoutRepository houseLayoutRepository;
    private final HouseRepository houseRepository;
    private final HouseService houseService;
    private final UserService userService;
    private final LayoutPermissionUtil layoutPermissionUtil;

    public HouseLayoutService(HouseLayoutRepository houseLayoutRepository, HouseRepository houseRepository,
                              HouseService houseService, UserService userService,
                              LayoutPermissionUtil layoutPermissionUtil) {
        this.houseLayoutRepository = houseLayoutRepository;
        this.houseRepository = houseRepository;
        this.houseService = houseService;
        this.userService = userService;
        this.layoutPermissionUtil = layoutPermissionUtil;
    }

    public HouseLayout createDraft(CreateLayoutRequest request) {
        House house = houseService.getHouseById(request.getHouseId());
        HouseLayout layout = new HouseLayout();
        layout.setHouse(house);
        layout.setLayoutIntent(request.getLayoutIntent());
        layout.setRedesignNotes(request.getRedesignNotes());
        layout.setLayoutStatus(HouseLayout.LayoutStatus.DRAFT);
        layout.setLayoutVersion(0);

        return houseLayoutRepository.save(layout);
    }


    public HouseLayout createLayout(CreateLayoutRequest request) {
        House house = houseService.getHouseById(request.getHouseId());
        User user = userService.getById(request.getUserId());

        HouseLayout layout = new HouseLayout();
        layout.setHouse(house);
        layout.setLayoutIntent(request.getLayoutIntent());

        if (user.getRole() == User.Role.USER) {
            layout.setLayoutVersion(1);
            layout.setLayoutStatus(HouseLayout.LayoutStatus.CONFIRMED);
        }

        if (user.getRole() == User.Role.DESIGNER) {

            int nextVersion = houseLayoutRepository
                    .findTopByHouseIdOrderByLayoutVersionDesc(house.getId())
                    .map(HouseLayout::getLayoutVersion)
                    .orElse(0) + 1;

            layout.setLayoutVersion(nextVersion);
            layout.setLayoutStatus(HouseLayout.LayoutStatus.SUBMITTED);
        }

        return houseLayoutRepository.save(layout);
    }

    public List<HouseLayout> getLayoutsByHouseId(Long houseId) {
        houseService.getHouseById(houseId);
        return houseLayoutRepository.findByHouseIdOrderByLayoutVersionDesc(houseId);
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
        User operator = userService.getById(userId);

        if (!layout.getHouse().getUser().getId().equals(userId)) {
            throw new RuntimeException("No permission to confirm this layout");
        }

        Optional<HouseLayout> existingConfirmed = houseLayoutRepository
                .findTopByHouseIdAndLayoutStatus(layout.getHouse().getId(), HouseLayout.LayoutStatus.CONFIRMED);

        if (existingConfirmed.isPresent()) {
            throw new RuntimeException("This house already has a confirmed layout");
        }

        layout.setLayoutStatus(HouseLayout.LayoutStatus.CONFIRMED);
        return houseLayoutRepository.save(layout);
    }

}
