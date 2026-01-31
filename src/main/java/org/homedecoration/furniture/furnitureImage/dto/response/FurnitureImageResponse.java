package org.homedecoration.furniture.furnitureImage.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.homedecoration.furniture.furnitureImage.entity.FurnitureImage;

import java.time.Instant;

@Getter
@Setter
public class FurnitureImageResponse {

    private Long imageId;
    private Long schemeId;
    private String imageUrl;
    private Instant createdAt;

    public static FurnitureImageResponse toDTO(FurnitureImage image) {
        FurnitureImageResponse dto = new FurnitureImageResponse();
        dto.setImageId(image.getId());
        dto.setSchemeId(image.getScheme().getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setCreatedAt(image.getCreatedAt());
        return dto;
    }
}
