package org.homedecoration.service;

import org.homedecoration.entity.House;
import org.homedecoration.repository.HouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseService {

    private final HouseRepository houseRepository;

    public HouseService(HouseRepository houseRepository) {
        this.houseRepository = houseRepository;
    }

    public House createHouse(House house) {
        return houseRepository.save(house);
    }

    public List<House> getAllHouses() {
        return houseRepository.findAll();
    }

    public House getHouseById(Long id) {
        return houseRepository.findById(id).orElse(null);
    }

    public House updateHouse(Long id, House house) {
        // 后续实现具体更新逻辑
        return null;
    }

    public void deleteHouse(Long id) {
        houseRepository.deleteById(id);
    }
}
