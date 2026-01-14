package org.homedecoration.house.service;

import org.homedecoration.house.dto.request.CreateHouseRequest;
import org.homedecoration.house.dto.request.UpdateHouseRequest;
import org.homedecoration.house.entity.House;
import org.homedecoration.house.repository.HouseRepository;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.identity.user.repository.UserRepository;
import org.homedecoration.layout.dto.response.HouseLayoutResponse;
import org.homedecoration.layout.entity.HouseLayout;
import org.homedecoration.layout.repository.HouseLayoutRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseService {


    private final HouseRepository houseRepository;
    private final UserRepository userRepository;
    private final HouseLayoutRepository layoutRepository;

    public HouseService(HouseRepository houseRepository, UserRepository userRepository,HouseLayoutRepository layoutRepository) {
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
        this.layoutRepository = layoutRepository;
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

    public void validCheck(Long houseId, Long userId){
        House house = getHouseById(houseId);
        if(!house.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权限操作该房源");
        }
        List<HouseLayout> layouts = layoutRepository.findAllByHouseId(houseId);
        for (HouseLayout layout : layouts) {
            if (layout.getLayoutStatus() != HouseLayout.LayoutStatus.DRAFT) {
                throw new RuntimeException("该房源的户型已发布，无法修改或删除");
            }
        }
    }

    public House updateHouse(UpdateHouseRequest request, Long houseId, Long userId) {
        validCheck(houseId,userId);
        House existing = getHouseById(houseId);

        existing.setCity(request.getCity());
        existing.setCommunityName(request.getCommunityName());
        existing.setBuildingNo(request.getBuildingNo());
        existing.setUnitNo(request.getUnitNo());
        existing.setRoomNo(request.getRoomNo());
        existing.setArea(request.getArea());
        existing.setLayoutType(request.getLayoutType());
        existing.setFloorCount(request.getFloorCount());

        return houseRepository.save(existing);
    }



    public void deleteHouse(Long houseId, Long userId) {
        validCheck(houseId,userId);
        houseRepository.deleteById(houseId);
    }

    public List<House> getAllHousesByUserId(Long userId) {
        return houseRepository.findAllByUserId(userId);
    }

}
