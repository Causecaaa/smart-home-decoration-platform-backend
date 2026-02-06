package org.homedecoration.stage.shopping.repository;

import org.homedecoration.stage.shopping.entity.StageCartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StageCartItemRepository extends JpaRepository<StageCartItem, Long> {

    List<StageCartItem> findByCartId(Long cartId);

    Optional<StageCartItem> findByCartIdAndProductId(Long cartId, Long productId);

    void deleteByCartId(Long cartId);
}
