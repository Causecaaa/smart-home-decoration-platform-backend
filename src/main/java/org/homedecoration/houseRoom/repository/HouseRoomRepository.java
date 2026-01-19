package org.homedecoration.houseRoom.repository;

import org.homedecoration.houseRoom.entity.HouseRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseRoomRepository extends JpaRepository<HouseRoom, Long> {

    // 使用JPA自动生成的查询方法
    List<HouseRoom> findByLayoutId(Long layoutId);
}
