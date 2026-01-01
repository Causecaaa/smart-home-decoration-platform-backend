package org.homedecoration.service;

import jakarta.servlet.http.HttpServletRequest;
import org.homedecoration.dto.request.CreateLayoutRequest;
import org.homedecoration.entity.House;
import org.homedecoration.entity.HouseLayout;
import org.homedecoration.entity.User;
import org.homedecoration.repository.HouseLayoutRepository;
import org.homedecoration.repository.HouseRepository;
import org.springframework.stereotype.Service;

@Service
public class HouseLayoutService {

    private final HouseLayoutRepository houseLayoutRepository;
    private final HouseRepository houseRepository;
    private final HouseService houseService;
    private final UserService userService;

    public HouseLayoutService(HouseLayoutRepository houseLayoutRepository, HouseRepository houseRepository,
                              HouseService houseService, UserService userService) {
        this.houseLayoutRepository = houseLayoutRepository;
        this.houseRepository = houseRepository;
        this.houseService = houseService;
        this.userService = userService;
    }

    public HouseLayout getHouseLayoutById(Long id) {
        return houseLayoutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HouseLayout not found with id: " + id));
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
}
