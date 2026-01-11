package org.homedecoration.identity.designer.repository;

import org.homedecoration.identity.designer.entity.Designer;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DesignerRepository extends JpaRepository<Designer, Long> {
    List<Designer> findByEnabledTrue(Sort sort);

    @Query("SELECT d FROM Designer d " +
            "WHERE d.enabled = true AND " +
            "(LOWER(d.realName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(d.style) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Designer> findByEnabledTrueAndKeyword(@Param("keyword") String keyword, Sort sort);

    List<Designer> findByVerifyStatus(Designer.VerifyStatus status);
}
