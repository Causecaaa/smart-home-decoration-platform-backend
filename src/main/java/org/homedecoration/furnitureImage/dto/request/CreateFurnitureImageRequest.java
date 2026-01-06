package org.homedecoration.furnitureImage.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreateFurnitureImageRequest {

    private MultipartFile file;

}