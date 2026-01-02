package org.homedecoration.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.homedecoration.entity.HouseLayoutImage;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreateLayoutImageRequest {
    private MultipartFile file;

    @NotBlank
    private String imageUrl; // 图片 URL（前端上传到 OSS / 七牛 / 本地之后传 URL）

    @NotNull
    private HouseLayoutImage.ImageType imageType; // USER / DESIGNER

    private String imageDesc; // 可选描述
}
