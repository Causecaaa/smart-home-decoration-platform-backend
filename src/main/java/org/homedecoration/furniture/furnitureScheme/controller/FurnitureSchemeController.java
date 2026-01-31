package org.homedecoration.furniture.furnitureScheme.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.furniture.furnitureScheme.dto.request.CreateFurnitureSchemeRequest;
import org.homedecoration.furniture.furnitureScheme.dto.response.FurnitureSchemeResponse;
import org.homedecoration.furniture.furnitureScheme.entity.FurnitureScheme;
import org.homedecoration.furniture.furnitureScheme.service.FurnitureSchemeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/furniture-scheme")
@RequiredArgsConstructor
public class FurnitureSchemeController {

    private final FurnitureSchemeService schemeService;
    private final JwtUtil jwtUtil;

    /** 创建房间方案（图片 + 材料信息） */
    @PostMapping("/room/{roomId}/create")
    public ApiResponse<FurnitureSchemeResponse> createScheme(
            HttpServletRequest request,
            @PathVariable Long roomId,
            @ModelAttribute CreateFurnitureSchemeRequest createRequest,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {

        Long designerId = jwtUtil.getUserId(request);
        createRequest.setRoomId(roomId);

        return ApiResponse.success(
                schemeService.createScheme(designerId, createRequest, file)
        );
    }

    /** 更新方案图片 */
    @PostMapping("/{schemeId}/update-image")
    public ApiResponse<FurnitureSchemeResponse> updateSchemeImage(
            HttpServletRequest request,
            @PathVariable Long schemeId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        Long designerId = jwtUtil.getUserId(request);
        return ApiResponse.success(
                schemeService.updateSchemeImage(schemeId, file, designerId)
        );
    }

    /** 查询房间所有方案 */
    @GetMapping("/{roomId}/get-all")
    public ApiResponse<List<FurnitureSchemeResponse>> listByRoom(@PathVariable Long roomId) {
        return ApiResponse.success(schemeService.listByRoom(roomId));
    }

    /** 查询单个方案 */
    @GetMapping("/{schemeId}/get")
    public ApiResponse<FurnitureSchemeResponse> getScheme(@PathVariable Long schemeId) {
        return ApiResponse.success(schemeService.getSchemeDetail(schemeId));
    }

    /** 确认方案 */
    @PutMapping("/{schemeId}/confirm")
    public ApiResponse<FurnitureSchemeResponse> confirmScheme(
            HttpServletRequest request,
            @PathVariable Long schemeId
    ) {
        Long userId = jwtUtil.getUserId(request);
        return ApiResponse.success(
                schemeService.confirmScheme(userId, schemeId)
        );
    }

    /** 删除方案 */
    @DeleteMapping("/{schemeId}")
    public ApiResponse<Void> deleteScheme(@PathVariable Long schemeId) {
        schemeService.deleteScheme(schemeId);
        return ApiResponse.success(null);
    }
}
