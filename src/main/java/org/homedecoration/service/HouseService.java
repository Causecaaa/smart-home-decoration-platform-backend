package org.homedecoration.service;

import org.homedecoration.dto.request.UpdateHouseRequest;
import org.homedecoration.entity.House;
import org.homedecoration.entity.User;
import org.homedecoration.repository.HouseRepository;
import org.homedecoration.repository.UserRepository;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new RuntimeException("User not found with id: " + house.getUser().getId()));

        house.setUser(user);

        return houseRepository.save(house);
    }


    public List<House> getAllHouses() {
        return houseRepository.findAll();
    }

    public House getHouseById(Long id) {
        return houseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("House not found with id: " + id));
    }


    public House updateHouse(UpdateHouseRequest request, Long id) {
        House existing = getHouseById(id);

        existing.setCity(request.getCity());
        existing.setCommunityName(request.getCommunityName());
        existing.setBuildingNo(request.getBuildingNo());
        existing.setUnitNo(request.getUnitNo());
        existing.setRoomNo(request.getRoomNo());
        existing.setArea(request.getArea());
        existing.setLayoutType(request.getLayoutType());
        existing.setFloorCount(request.getFloorCount());
        existing.setDecorationType(request.getDecorationType());

        return houseRepository.save(existing);
    }



    public void deleteHouse(Long id) {
        houseRepository.deleteById(id);
    }
}
