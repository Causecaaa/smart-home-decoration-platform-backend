package org.homedecoration.service;

import org.homedecoration.entity.HouseLayoutImage;
import org.homedecoration.repository.HouseLayoutImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseLayoutImageService {

    private final HouseLayoutImageRepository houseLayoutImageRepository;

    public HouseLayoutImageService(HouseLayoutImageRepository houseLayoutImageRepository) {
        this.houseLayoutImageRepository = houseLayoutImageRepository;
    }

    public List<HouseLayoutImage> findByLayoutId(Long layoutId) {
        return null;
    }

    public HouseLayoutImage save(HouseLayoutImage image) {
        return null;
    }
}
