package org.homedecoration.houseRoom.repository;

import org.homedecoration.houseRoom.entity.HouseRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HouseRoomRepository extends JpaRepository<HouseRoom, Long> {

    List<HouseRoom> findByLayoutId(Long layoutId);
}
