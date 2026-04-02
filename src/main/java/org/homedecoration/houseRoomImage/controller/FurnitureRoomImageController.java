package org.homedecoration.houseRoomImage.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.houseRoomImage.dto.request.CreateFurnitureRoomImageRequest;
import org.homedecoration.houseRoomImage.dto.response.FurnitureRoomImageResponse;
import org.homedecoration.houseRoomImage.entity.FurnitureRoomImage;
import org.homedecoration.houseRoomImage.service.FurnitureRoomImageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/room-image")
@RequiredArgsConstructor
public class FurnitureRoomImageController {

    private final FurnitureRoomImageService roomImageService;
    private final JwtUtil jwtUtil;

    @PostMapping("/{roomId}/create")
    public ApiResponse<FurnitureRoomImageResponse> createImage(
            @PathVariable Long roomId,
            @Valid @RequestBody CreateFurnitureRoomImageRequest request,
            HttpServletRequest httpRequest
    ) {
        Long designerId = jwtUtil.getUserId(httpRequest);
        FurnitureRoomImage image = roomImageService.createImage(roomId, designerId, request);
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
