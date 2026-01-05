package org.homedecoration.furnitureImage.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.homedecoration.furnitureImage.entity.FurnitureImage;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreateFurnitureImageRequest {

    private MultipartFile file;

    private FurnitureImage.ImageType imageType;

    /**
     * 前端直接传的图片 URL（可选）
     */
    private String imageUrl;
}