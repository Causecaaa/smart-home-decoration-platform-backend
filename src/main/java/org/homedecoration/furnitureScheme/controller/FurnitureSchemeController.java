package org.homedecoration.furnitureScheme.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.furnitureScheme.dto.request.CreateFurnitureSchemeRequest;
import org.homedecoration.furnitureScheme.dto.response.FurnitureSchemeResponse;
import org.homedecoration.furnitureScheme.entity.FurnitureScheme;
import org.homedecoration.furnitureScheme.service.FurnitureSchemeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/furniture-scheme")
@RequiredArgsConstructor
public class FurnitureSchemeController {

    private final FurnitureSchemeService schemeService;
    private final JwtUtil jwtUtil;

    @PostMapping("/create")
    public ApiResponse<FurnitureSchemeResponse> createScheme(
            HttpServletRequest request,
            @RequestBody CreateFurnitureSchemeRequest createRequest
    ) {
        Long designerId = jwtUtil.getUserId(request);
        FurnitureScheme scheme = schemeService.createScheme(designerId, createRequest);
        return ApiResponse.success(FurnitureSchemeResponse.toDTO(scheme));
    }

    /** 查询房间所有方案 */
    @GetMapping("/{roomId}/get-all")
    public ApiResponse<List<FurnitureSchemeResponse>> listByRoom(@PathVariable Long roomId) {
        List<FurnitureScheme> schemes = schemeService.listByRoom(roomId);
        return ApiResponse.success(
                schemes.stream().map(FurnitureSchemeResponse::toDTO).collect(Collectors.toList())
        );
    }

    /** 查询单个方案 */
    @GetMapping("/{schemeId}/get")
    public ApiResponse<FurnitureSchemeResponse> getScheme(@PathVariable Long schemeId) {
        FurnitureScheme scheme = schemeService.getById(schemeId);
        return ApiResponse.success(FurnitureSchemeResponse.toDTO(scheme));
    }

    /** 更新方案状态 */
    @PutMapping("/{schemeId}/confirm")
    public ApiResponse<FurnitureSchemeResponse> confirmScheme(
            HttpServletRequest request,
            @PathVariable Long schemeId
    ) {
        Long userId = jwtUtil.getUserId(request);
        FurnitureScheme scheme = schemeService.confirmScheme(userId, schemeId);
        return ApiResponse.success(FurnitureSchemeResponse.toDTO(scheme));
    }

    /** 删除方案 */
    @DeleteMapping("/{schemeId}")
    public ApiResponse<Void> deleteScheme(@PathVariable Long schemeId) {
        schemeService.deleteScheme(schemeId);
        return ApiResponse.success(null);
    }
}