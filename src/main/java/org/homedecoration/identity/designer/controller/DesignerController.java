package org.homedecoration.identity.designer.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.identity.designer.dto.request.CreateDesignerRequest;
import org.homedecoration.identity.designer.dto.request.UpdateDesignerProfileRequest;
import org.homedecoration.identity.designer.dto.response.DesignerDetailResponse;
import org.homedecoration.identity.designer.dto.response.DesignerSimpleResponse;
import org.homedecoration.identity.designer.service.DesignerService;
import org.homedecoration.layout.dto.response.HouseLayoutResponse;
import org.homedecoration.layout.entity.HouseLayout;
import org.homedecoration.layout.service.HouseLayoutService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/designer")
public class DesignerController {

    private final DesignerService designerService;
    private final JwtUtil jwtUtil;

    public DesignerController(DesignerService designerService, JwtUtil jwtUtil) {
        this.designerService = designerService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 申请成为设计师
     */
    @PostMapping("/apply")
    public ApiResponse<DesignerDetailResponse> applyDesigner(
            HttpServletRequest request,
            @Valid @RequestBody CreateDesignerRequest createDesignerRequest) {

        Long userId = jwtUtil.getUserId(request);

        return ApiResponse.success(
                DesignerDetailResponse.toDTO(
                        designerService.apply(userId, createDesignerRequest)
                )
        );
    }

    /**
     * 获取当前登录设计师信息
     */
    @GetMapping("/get")
    public ApiResponse<DesignerDetailResponse> getMyDesignerInfo(HttpServletRequest request) {

        Long userId = jwtUtil.getUserId(request);

        return ApiResponse.success(
                DesignerDetailResponse.toDTO(
                        designerService.getByUserId(userId)
                )
        );
    }


    @PutMapping("/update")
    public ApiResponse<DesignerDetailResponse> updateMyProfile(
            HttpServletRequest request,
            @Valid @RequestBody UpdateDesignerProfileRequest body) {

        Long userId = jwtUtil.getUserId(request);

        return ApiResponse.success(
                DesignerDetailResponse.toDTO(
                        designerService.updateProfile(userId, body)
                )
        );
    }


    @GetMapping("/{userId}/get")
    public ApiResponse<DesignerDetailResponse> getByUserId(@PathVariable Long userId) {
        return ApiResponse.success(
                DesignerDetailResponse.toDTO(
                        designerService.getByUserId(userId)
                )
        );
    }

    @GetMapping("/list")
    public ApiResponse<List<DesignerSimpleResponse>> listDesigners(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "rating") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String order
    ) {
        return ApiResponse.success(
                designerService.list(keyword, sortBy, order)
                        .stream()
                        .map(DesignerSimpleResponse::toDTO)
                        .toList()
        );
    }

    @GetMapping("/layouts")
    public ApiResponse<List<HouseLayout>> getPendingLayouts(HttpServletRequest request) {
        Long designerId = jwtUtil.getUserId(request);  // token获取设计师ID
        List<HouseLayout> pendingLayouts = designerService.getPendingLayoutsForDesigner(designerId);
        return ApiResponse.success(pendingLayouts);
    }

    @GetMapping("/furniture/layouts")
    public ApiResponse<List<HouseLayout>> getPendingFurnitureLayouts(HttpServletRequest request) {
        Long designerId = jwtUtil.getUserId(request);  // token 获取设计师 ID
        List<HouseLayout> layouts = designerService.getPendingFurnitureLayoutsForDesigner(designerId);
        return ApiResponse.success(layouts);
    }

}
