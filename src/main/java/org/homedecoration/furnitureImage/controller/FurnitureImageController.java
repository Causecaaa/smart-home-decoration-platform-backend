package org.homedecoration.furnitureImage.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.furnitureImage.dto.request.CreateFurnitureImageRequest;
import org.homedecoration.furnitureImage.dto.response.FurnitureImageResponse;
import org.homedecoration.furnitureImage.entity.FurnitureImage;
import org.homedecoration.furnitureImage.service.FurnitureImageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/furniture-image")
@RequiredArgsConstructor
public class FurnitureImageController {

    private final FurnitureImageService furnitureImageService;
    private final JwtUtil jwtUtil;

    @PostMapping("/{schemeId}/create")
    public ApiResponse<FurnitureImageResponse> createImage(
            @PathVariable Long schemeId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpRequest
    ) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("必须上传文件");
        }

        Long designerId = jwtUtil.getUserId(httpRequest);

        FurnitureImage image = furnitureImageService.createImage(schemeId, file, designerId);

        return ApiResponse.success(FurnitureImageResponse.toDTO(image));
    }

    @DeleteMapping("/{schemeId}/image")
    public ApiResponse<Void> deleteImage(
            @PathVariable Long schemeId,
            HttpServletRequest httpRequest
    ) {
        Long designerId = jwtUtil.getUserId(httpRequest);
        furnitureImageService.deleteImage(schemeId, designerId);
        return ApiResponse.success(null);
    }

}
