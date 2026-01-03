package org.homedecoration.house.service;

import org.homedecoration.house.dto.request.CreateHouseRequest;
import org.homedecoration.house.dto.request.UpdateHouseRequest;
import org.homedecoration.house.entity.House;
import org.homedecoration.user.entity.User;
import org.homedecoration.house.repository.HouseRepository;
import org.homedecoration.user.repository.UserRepository;
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

    public House createHouse(CreateHouseRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

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

    public List<House> getAllHousesByUserId(Long userId) {
        return houseRepository.findAllByUserId(userId);
    }

}
