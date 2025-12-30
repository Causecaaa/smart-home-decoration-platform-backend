package org.homedecoration.repository;

import org.homedecoration.entity.HouseLayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseLayoutRepository extends JpaRepository<HouseLayout, Long> {
    // 根据房屋查布局
    List<HouseLayout> findByHouseId(Long houseId);
}
