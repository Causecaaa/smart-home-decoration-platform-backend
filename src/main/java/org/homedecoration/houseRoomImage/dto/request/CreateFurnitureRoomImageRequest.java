package org.homedecoration.houseRoomImage.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFurnitureRoomImageRequest {

    @NotBlank
    private String imageUrl;
}
