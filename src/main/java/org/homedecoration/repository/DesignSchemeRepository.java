package org.homedecoration.repository;

import org.homedecoration.entity.DesignScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DesignSchemeRepository extends JpaRepository<DesignScheme, Long> {
    // 根据布局查设计方案
    List<DesignScheme> findByLayoutId(Long layoutId);
}
