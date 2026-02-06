package org.homedecoration.stage.shopping.repository;

import org.homedecoration.stage.shopping.entity.StageOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StageOrderItemRepository extends JpaRepository<StageOrderItem, Long> {

    List<StageOrderItem> findByStageOrderId(Long stageOrderId);
}
