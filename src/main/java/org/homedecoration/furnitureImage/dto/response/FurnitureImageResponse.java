package org.homedecoration.furnitureImage.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.homedecoration.furnitureImage.entity.FurnitureImage;

import java.time.Instant;

@Getter
@Setter
public class FurnitureImageResponse {

    private Long imageId;
    private Long schemeId;
    private String imageUrl;
    private FurnitureImage.ImageType imageType;
    private Instant createdAt;

    public static FurnitureImageResponse toDTO(FurnitureImage image) {
        FurnitureImageResponse dto = new FurnitureImageResponse();
        dto.setImageId(image.getId());
        dto.setSchemeId(image.getScheme().getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setImageType(image.getImageType());
        dto.setCreatedAt(image.getCreatedAt());
        return dto;
    }
}
