//package org.homedecoration.furnitureImage.controller;
//
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.homedecoration.common.response.ApiResponse;
//import org.homedecoration.common.utils.JwtUtil;
//import org.homedecoration.furnitureImage.dto.request.CreateFurnitureImageRequest;
//import org.homedecoration.furnitureImage.dto.response.FurnitureImageResponse;
//import org.homedecoration.furnitureImage.entity.FurnitureImage;
//import org.homedecoration.furnitureImage.service.FurnitureImageService;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
//@RestController
//@RequestMapping("/api/furniture-schemes")
//@RequiredArgsConstructor
//public class FurnitureImageController {
//
//    private final FurnitureImageService furnitureImageService;
//    private final JwtUtil jwtUtil;
//
//    /**
//     * 给方案新增图片
//     */
//    @PostMapping("/{schemeId}/images")
//    public ApiResponse<FurnitureImageResponse> createImage(
//            @PathVariable Long schemeId,
//            @RequestParam(value = "file", required = false) MultipartFile file,
//            @RequestParam(value = "imageType", required = false)
//            FurnitureImage.ImageType imageType,
//            @RequestParam(value = "imageUrl", required = false) String imageUrl,
//            HttpServletRequest httpRequest
//    ) throws IOException {
//
//        Long userId = jwtUtil.getUserId(httpRequest);
//
//        CreateFurnitureImageRequest request = new CreateFurnitureImageRequest();
//        request.setFile(file);
//        request.setImageType(imageType);
//        request.setImageUrl(imageUrl);
//
//        FurnitureImage image =
//                furnitureImageService.createImage(schemeId, request, userId);
//
//        return ApiResponse.success(FurnitureImageResponse.toDTO(image));
//    }
//}
