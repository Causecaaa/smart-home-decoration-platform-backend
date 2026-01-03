package org.homedecoration.house.repository;

import jakarta.validation.constraints.NotNull;
import org.homedecoration.house.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {
    // 按用户查房屋
    List<House> findByUserId(Long userId);

    void getHouseById(@NotNull Long houseId);

    List<House> findAllByUserId(Long userId);
}
