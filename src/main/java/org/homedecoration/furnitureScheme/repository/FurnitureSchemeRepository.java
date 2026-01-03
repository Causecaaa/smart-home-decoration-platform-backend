package org.homedecoration.furnitureScheme.repository;

import org.homedecoration.furnitureScheme.entity.FurnitureScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FurnitureSchemeRepository extends JpaRepository<FurnitureScheme, Long> {
    // 根据布局查设计方案
    List<FurnitureScheme> findByLayoutId(Long layoutId);
}
