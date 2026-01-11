package org.homedecoration.identity.admin.repository;

import org.homedecoration.identity.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    // 检查一个用户是否是管理员
    default boolean isAdmin(Long userId) {
        return existsById(userId);
    }
}
