package org.homedecoration.service;

import org.homedecoration.dto.request.CreateUserLayoutRequest;
import org.homedecoration.entity.House;
import org.homedecoration.entity.HouseLayout;
import org.homedecoration.repository.HouseLayoutRepository;
import org.homedecoration.repository.HouseRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class HouseLayoutService {

    private final HouseLayoutRepository houseLayoutRepository;
    private final HouseRepository houseRepository;

    public HouseLayoutService(HouseLayoutRepository houseLayoutRepository, HouseRepository houseRepository) {
        this.houseLayoutRepository = houseLayoutRepository;
        this.houseRepository = houseRepository;
    }


    public HouseLayout createByUser(CreateUserLayoutRequest dto) {

        House house = houseRepository.findById(dto.getHouseId())
                .orElseThrow(() -> new RuntimeException("房屋不存在"));

        HouseLayout layout = new HouseLayout();
        layout.setHouse(house);
        layout.setLayoutIntent(dto.getLayoutIntent());
        layout.setRedesignNotes(dto.getRedesignNotes());
        layout.setLayoutStatus(HouseLayout.LayoutStatus.DRAFT);
        layout.setCreatedAt(Instant.now());
        layout.setUpdatedAt(Instant.now());

        return houseLayoutRepository.save(layout);
    }


}
