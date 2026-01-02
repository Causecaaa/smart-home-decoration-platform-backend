package org.homedecoration.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.homedecoration.common.ApiResponse;
import org.homedecoration.dto.request.CreateLayoutImageRequest;
import org.homedecoration.dto.response.HouseLayoutImageResponse;
import org.homedecoration.entity.HouseLayoutImage;
import org.homedecoration.service.HouseLayoutImageService;
import org.homedecoration.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/house-layout-image")
public class HouseLayoutImageController {

    private final HouseLayoutImageService houseLayoutImageService;
    private final JwtUtil jwtUtil;

    public HouseLayoutImageController(HouseLayoutImageService houseLayoutImageService, JwtUtil jwtUtil) {
        this.houseLayoutImageService = houseLayoutImageService;
        this.jwtUtil = jwtUtil;
    }

    // 上传图片
    @PostMapping("/{layoutId}/images")
    public ApiResponse<HouseLayoutImageResponse> createImage(
            @PathVariable Long layoutId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "imageType", required = false) HouseLayoutImage.ImageType imageType,
            @RequestParam(value = "imageDesc", required = false) String imageDesc,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            HttpServletRequest httpRequest) throws IOException {

        Long userId = jwtUtil.getUserId(httpRequest);

        CreateLayoutImageRequest request = new CreateLayoutImageRequest();
        request.setFile(file);                // MultipartFile
        request.setImageType(imageType);      // 可选
        request.setImageDesc(imageDesc);      // 可选
        request.setImageUrl(imageUrl);        // 可选，前端直接提供的 URL

        HouseLayoutImage image = houseLayoutImageService.createImage(layoutId, request, userId);

        return ApiResponse.success(HouseLayoutImageResponse.toDTO(image));
    }



}
