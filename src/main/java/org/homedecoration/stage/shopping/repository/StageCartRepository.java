package org.homedecoration.stage.shopping.repository;

import org.homedecoration.stage.shopping.entity.StageCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StageCartRepository extends JpaRepository<StageCart, Long> {

    Optional<StageCart> findByStageIdAndUserId(Long stageId, Long userId);
}
