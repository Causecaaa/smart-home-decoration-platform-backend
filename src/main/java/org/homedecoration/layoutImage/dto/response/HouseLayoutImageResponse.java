package org.homedecoration.layoutImage.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.homedecoration.layoutImage.entity.HouseLayoutImage;
import org.homedecoration.layoutImage.entity.HouseLayoutImage.ImageType;

import java.time.Instant;

@Getter
@Setter
public class HouseLayoutImageResponse {
    private Long userId;
    private Long houseId;
    private Long layoutId;
    private Long imageId;
    private String imageUrl;
    private ImageType imageType;
    private String imageDesc;
    private Instant createdAt;
    private Instant updatedAt;

    public static HouseLayoutImageResponse toDTO(HouseLayoutImage image) {
        HouseLayoutImageResponse dto = new HouseLayoutImageResponse();
        dto.setUserId(image.getLayout().getHouse().getUser().getId());
        dto.setHouseId(image.getLayout().getHouse().getId());
        dto.setLayoutId(image.getLayout().getId());
        dto.setImageId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setImageType(image.getImageType() != null ? image.getImageType() : ImageType.ORIGINAL);
        dto.setImageDesc(image.getImageDesc());
        dto.setCreatedAt(image.getCreatedAt());
        dto.setUpdatedAt(image.getUpdatedAt());
        return dto;
    }
}
