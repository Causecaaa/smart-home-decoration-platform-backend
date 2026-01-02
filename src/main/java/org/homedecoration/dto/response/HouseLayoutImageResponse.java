package org.homedecoration.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.homedecoration.entity.HouseLayoutImage;
import org.homedecoration.entity.HouseLayoutImage.ImageType;

import java.time.Instant;

@Getter
@Setter
public class HouseLayoutImageResponse {
    private Long id;
    private String imageUrl;
    private ImageType imageType;
    private String imageDesc;
    private Instant createdAt;
    private Instant updatedAt;

    public static HouseLayoutImageResponse toDTO(HouseLayoutImage image) {
        HouseLayoutImageResponse dto = new HouseLayoutImageResponse();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setImageType(image.getImageType() != null ? image.getImageType() : ImageType.ORIGINAL);
        dto.setImageDesc(image.getImageDesc());
        dto.setCreatedAt(image.getCreatedAt());
        dto.setUpdatedAt(image.getUpdatedAt());
        return dto;
    }
}
