package org.homedecoration.furnitureImage.repository;

import org.homedecoration.furnitureImage.entity.FurnitureImage;
import org.homedecoration.furnitureScheme.entity.FurnitureScheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FurnitureImageRepository
        extends JpaRepository<FurnitureImage, Long> {

    List<FurnitureImage> findBySchemeOrderByCreatedAtAsc(FurnitureScheme scheme);

    Optional<FurnitureImage> findByScheme(FurnitureScheme scheme);
}
