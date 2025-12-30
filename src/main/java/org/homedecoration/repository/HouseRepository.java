package org.homedecoration.repository;

import org.homedecoration.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {
    // 按用户查房屋
    List<House> findByUserId(Long userId);
}
