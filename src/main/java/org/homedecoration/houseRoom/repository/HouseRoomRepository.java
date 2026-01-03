package org.homedecoration.houseRoom.repository;

import org.homedecoration.houseRoom.entity.HouseRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseRoomRepository extends JpaRepository<HouseRoom, Long> {
    // 根据布局查房间列表
    List<HouseRoom> findByLayoutId(Long layoutId);

    // 可按楼层查
    List<HouseRoom> findByLayoutIdAndFloorNo(Long layoutId, Integer floorNo);
}
