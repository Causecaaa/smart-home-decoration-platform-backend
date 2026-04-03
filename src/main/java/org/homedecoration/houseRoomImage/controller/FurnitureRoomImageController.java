package org.homedecoration.houseRoomImage.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.houseRoomImage.dto.response.FurnitureRoomImageResponse;
import org.homedecoration.houseRoomImage.entity.FurnitureRoomImage;
import org.homedecoration.houseRoomImage.service.FurnitureRoomImageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/room-image")
@RequiredArgsConstructor
public class FurnitureRoomImageController {

    private final FurnitureRoomImageService roomImageService;
    private final JwtUtil jwtUtil;

    @GetMapping("/{roomId}/list")
    public ApiResponse<FurnitureRoomImageResponse[]> listImages(
            @PathVariable Long roomId
    ) {
        FurnitureRoomImage[] images = roomImageService.listImages(roomId);
        return ApiResponse.success(FurnitureRoomImageResponse.toDTOs(images));
    }

    @PostMapping("/{roomId}/create")
    public ApiResponse<FurnitureRoomImageResponse> createImage(
            @PathVariable Long roomId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpRequest
    ) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("必须上传文件");
        }

        Long designerId = jwtUtil.getUserId(httpRequest);
        FurnitureRoomImage image = roomImageService.createImage(roomId, designerId, file);
        return ApiResponse.success(FurnitureRoomImageResponse.toDTO(image));
    }

    @DeleteMapping("/{roomImageId}/delete")
    public ApiResponse<Void> deleteImage(
            @PathVariable Long roomImageId,
            HttpServletRequest httpRequest
    ) {
        Long designerId = jwtUtil.getUserId(httpRequest);
        roomImageService.deleteImage(roomImageId, designerId);
        return ApiResponse.success(null);
    }
}
