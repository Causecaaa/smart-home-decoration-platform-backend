package org.homedecoration.layout.repository;

import org.homedecoration.layout.entity.HouseLayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseLayoutRepository extends JpaRepository<HouseLayout, Long> {
    // 根据房屋查布局
    List<HouseLayout> findByHouseId(Long houseId);

    Optional<HouseLayout> findTopByHouseIdOrderByLayoutVersionDesc(Long houseId);

    List<HouseLayout> findByHouseIdOrderByLayoutVersionDesc(Long houseId);

    Optional<HouseLayout> findTopByHouseIdAndLayoutStatus(Long id, HouseLayout.LayoutStatus layoutStatus);
}
