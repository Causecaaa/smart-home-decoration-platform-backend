package org.homedecoration.layout.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Data;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.layout.dto.request.CreateLayoutRequest;
import org.homedecoration.layout.dto.request.UpdateLayoutRequest;
import org.homedecoration.layout.dto.response.CurrentLayoutResponse;
import org.homedecoration.layout.dto.response.HouseLayoutResponse;
import org.homedecoration.layout.dto.response.LayoutOverviewResponse;
import org.homedecoration.layout.entity.HouseLayout;
import org.homedecoration.layout.service.HouseLayoutService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/house-layout")
public class HouseLayoutController {

    private final HouseLayoutService houseLayoutService;
    private final JwtUtil jwtUtil;

    public HouseLayoutController(HouseLayoutService houseLayoutService, JwtUtil jwtUtil) {
        this.houseLayoutService = houseLayoutService;
        this.jwtUtil = jwtUtil;
    }

    // 用户REDESIGN 创建提要求
    @PostMapping("/create-draft")
    public ApiResponse<CurrentLayoutResponse> createDraft(
            @RequestBody CreateLayoutRequest request,
            HttpServletRequest httpRequest) {

        Long userId = jwtUtil.getUserId(httpRequest);

        return ApiResponse.success(
                houseLayoutService.createDraft(request, userId)
        );
    }

    @GetMapping("/overview/{houseId}")
    public ApiResponse<LayoutOverviewResponse> getLayoutOverview(
            @PathVariable Long houseId,
            HttpServletRequest request
    ) {
        Long userId = jwtUtil.getUserId(request);
        return ApiResponse.success(
                houseLayoutService.getLayoutOverview(houseId, userId)
        );
    }


    // 用户KEEP_ORIGINAL 创建confirmed 或者 设计师计划
    @PostMapping("/create-layout")
    public ApiResponse<HouseLayoutResponse> createLayout(
            @RequestBody @Valid CreateLayoutRequest request,
            HttpServletRequest httpRequest) {
        System.out.println("**************************************************");
        request.setUserId(jwtUtil.getUserId(httpRequest));

        System.out.println(request.getUserId());
        return ApiResponse.success(
                HouseLayoutResponse.toDTO(
                        houseLayoutService.createLayout(request)
                )
        );
    }

    @GetMapping("/{houseId}/get-all")
    public ApiResponse<List<HouseLayoutResponse>> getLayoutsByHouse(
            @PathVariable Long houseId) {
        return ApiResponse.success(
                houseLayoutService.getLayoutsByHouseId(houseId)
                        .stream()
                        .map(HouseLayoutResponse::toDTO)
                        .toList()
        );
    }

    @GetMapping("/get/{layoutId}")
    public ApiResponse<HouseLayoutResponse> getLayoutDetail(
            @PathVariable Long layoutId) {

        HouseLayout layout = houseLayoutService.getLayoutById(layoutId);
        return ApiResponse.success(
                HouseLayoutResponse.toDTO(layout)
        );
    }

    @PutMapping("/update/{layoutId}")
    public ApiResponse<HouseLayoutResponse> updateLayout(
            @PathVariable Long layoutId,
            @RequestBody @Valid UpdateLayoutRequest request,
            HttpServletRequest httpRequest) {

        String token = httpRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.getUserId(token);

        return ApiResponse.success(
                HouseLayoutResponse.toDTO(
                        houseLayoutService.updateLayout(layoutId, request, userId)
                )
        );
    }

    @DeleteMapping("/delete/{layoutId}")
    public ApiResponse<Void> deleteLayout(
            @PathVariable Long layoutId,
            HttpServletRequest httpRequest) {

        String token = httpRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.getUserId(token);

        houseLayoutService.deleteLayout(layoutId, userId);

        return ApiResponse.success(null);
    }

    @PutMapping("/{layoutId}/confirm")
    public ApiResponse<HouseLayoutResponse> confirmLayout(
            @PathVariable Long layoutId,
            HttpServletRequest httpRequest) {

        String token = httpRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.getUserId(token);

        HouseLayout confirmedLayout = houseLayoutService.confirmLayout(layoutId, userId);
        return ApiResponse.success(HouseLayoutResponse.toDTO(confirmedLayout));
    }

    @Data
    public static class ConfirmFurnitureDesignerRequest {
        private Long furnitureDesignerId;
    }

    @PutMapping("/{layoutId}/confirm/furnitureDesigner")
    public ApiResponse<HouseLayoutResponse> confirmFurnitureDesigner(
            @PathVariable Long layoutId,
            @RequestBody ConfirmFurnitureDesignerRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = jwtUtil.getUserId(httpRequest);

        HouseLayout updatedLayout = houseLayoutService.confirmFurnitureDesigner(
                layoutId,
                request.getFurnitureDesignerId(),
                userId
        );

        return ApiResponse.success(HouseLayoutResponse.toDTO(updatedLayout));
    }


}
