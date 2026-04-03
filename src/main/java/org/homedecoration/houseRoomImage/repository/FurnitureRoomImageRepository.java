package org.homedecoration.houseRoomImage.repository;

import org.homedecoration.houseRoomImage.entity.FurnitureRoomImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface FurnitureRoomImageRepository extends JpaRepository<FurnitureRoomImage, Long> {
    Collection<Object> findByRoomId(Long roomId);
}
