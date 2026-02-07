package org.homedecoration.stage.stage.repository;

import org.homedecoration.stage.stage.entity.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {

    List<Stage> findByHouseId(Long houseId);

    List<Stage> findByHouseIdOrderByOrderAsc(Long houseId);

    Optional<Object> findByHouseIdAndOrder(Long houseId, Integer order);

    List<Stage> findByHouseIdAndOrderLessThan(Long houseId, int order);

    List<Stage> findByHouseIdAndOrderGreaterThan(Long houseId, int order);

    boolean existsByHouseId(Long houseId);
}
