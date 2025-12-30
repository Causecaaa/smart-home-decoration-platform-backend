package org.homedecoration.service;

import org.homedecoration.entity.HouseLayout;
import org.homedecoration.repository.HouseLayoutRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseLayoutService {

    private final HouseLayoutRepository houseLayoutRepository;

    public HouseLayoutService(HouseLayoutRepository houseLayoutRepository) {
        this.houseLayoutRepository = houseLayoutRepository;
    }

    public List<HouseLayout> findByHouseId(Long houseId) {
        return null;
    }

    public HouseLayout save(HouseLayout layout) {
        return null;
    }
}
