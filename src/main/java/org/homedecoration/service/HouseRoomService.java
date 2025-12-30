package org.homedecoration.service;

import org.homedecoration.entity.HouseRoom;
import org.homedecoration.repository.HouseRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseRoomService {

    private final HouseRoomRepository houseRoomRepository;

    public HouseRoomService(HouseRoomRepository houseRoomRepository) {
        this.houseRoomRepository = houseRoomRepository;
    }

    public List<HouseRoom> findByLayoutId(Long layoutId) {
        return null;
    }

    public HouseRoom save(HouseRoom room) {
        return null;
    }
}
