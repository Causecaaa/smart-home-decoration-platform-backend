package org.homedecoration.layout.repository;

import org.homedecoration.layout.entity.HouseLayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface HouseLayoutRepository extends JpaRepository<HouseLayout, Long> {
    // 根据房屋查布局
    List<HouseLayout> findByHouseId(Long houseId);

    Optional<HouseLayout> findTopByHouseIdOrderByLayoutVersionDesc(Long houseId);

    List<HouseLayout> findByHouseIdOrderByLayoutVersionDesc(Long houseId);

    Optional<HouseLayout> findTopByHouseIdAndLayoutStatus(Long id, HouseLayout.LayoutStatus layoutStatus);

    List<HouseLayout> findByHouseIdAndIdNot(Long id, Long layoutId);

    List<HouseLayout> findByHouseIdOrderByLayoutVersionAsc(Long houseId);

    List<HouseLayout> findByDesignerIdAndLayoutStatus(Long designerId, HouseLayout.LayoutStatus status);

    List<HouseLayout> findByFurnitureDesignerIdAndLayoutStatus(Long designerId, HouseLayout.LayoutStatus layoutStatus);

    List<HouseLayout> findAllByHouseId(Long houseId);

    boolean existsByHouseIdAndLayoutStatus(Long houseId, HouseLayout.LayoutStatus layoutStatus);

    Long getHouseLayoutById(Long id);

    Optional<Object> findByHouseIdAndLayoutVersion(Long houseId, int i);

    Collection<Object> findByHouseIdAndLayoutVersionGreaterThan(Long houseId, int i);
}
