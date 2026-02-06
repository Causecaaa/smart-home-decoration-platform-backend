package org.homedecoration.stage.shopping.repository;

import org.homedecoration.stage.shopping.entity.StageOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface StageOrderRepository extends JpaRepository<StageOrder, Long> {

    List<StageOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    Collection<Object> findByStageIdAndUserIdOrderByCreatedAtDesc(Long stageId, Long userId);

    List<StageOrder> findByStageIdAndUserIdAndStatus(Long stageId, Long userId, StageOrder.OrderStatus orderStatus);
}
