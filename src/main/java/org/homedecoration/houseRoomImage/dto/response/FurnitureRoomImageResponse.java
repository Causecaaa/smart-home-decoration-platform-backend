package org.homedecoration.houseRoomImage.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.homedecoration.houseRoomImage.entity.FurnitureRoomImage;

import java.time.Instant;

@Getter
@Setter
public class FurnitureRoomImageResponse {

    private Long roomImageId;
    private Long roomId;
    private String imageUrl;
    private Instant createdAt;

    public static FurnitureRoomImageResponse toDTO(FurnitureRoomImage image) {
        FurnitureRoomImageResponse dto = new FurnitureRoomImageResponse();
        dto.setRoomImageId(image.getId());
        dto.setRoomId(image.getRoom().getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setCreatedAt(image.getCreatedAt());
        return dto;
    }
}
