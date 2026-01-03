package org.homedecoration.layoutImage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.homedecoration.layoutImage.entity.HouseLayoutImage;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreateLayoutImageRequest {
    private MultipartFile file;

    @NotBlank
    private String imageUrl;

    @NotNull
    private HouseLayoutImage.ImageType imageType; // USER / DESIGNER

    private String imageDesc; // 可选描述
}
