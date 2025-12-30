package org.homedecoration.service;

import org.homedecoration.entity.House;
import org.homedecoration.entity.User;
import org.homedecoration.repository.HouseRepository;
import org.homedecoration.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class HouseService {


    private final HouseRepository houseRepository;
    private final UserRepository userRepository;

    public HouseService(HouseRepository houseRepository, UserRepository userRepository) {
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
    }

    public House createHouse(House house) {
        User user = userRepository.findById(house.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        house.setUser(user);

        house.setCreatedAt(Instant.now());
        house.setUpdatedAt(Instant.now());

        return houseRepository.save(house);
    }


    public List<House> getAllHouses() {
        return houseRepository.findAll();
    }

    public House getHouseById(Long id) {
        return houseRepository.findById(id).orElse(null);
    }

    public House updateHouse(Long id, House house) {
        return houseRepository.findById(id).map(existing -> {
            existing.setCity(house.getCity());
            existing.setCommunityName(house.getCommunityName());
            existing.setBuildingNo(house.getBuildingNo());
            existing.setUnitNo(house.getUnitNo());
            existing.setRoomNo(house.getRoomNo());
            existing.setArea(house.getArea());
            existing.setLayoutType(house.getLayoutType());
            existing.setFloorCount(house.getFloorCount());
            existing.setDecorationType(house.getDecorationType());
            existing.setUpdatedAt(house.getUpdatedAt() != null ? house.getUpdatedAt() : java.time.Instant.now());
            return houseRepository.save(existing);
        }).orElse(null);
    }


    public void deleteHouse(Long id) {
        houseRepository.deleteById(id);
    }
}
