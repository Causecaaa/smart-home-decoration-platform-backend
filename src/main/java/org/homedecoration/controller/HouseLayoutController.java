package org.homedecoration.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.homedecoration.common.ApiResponse;
import org.homedecoration.utils.JwtUtil;
import org.homedecoration.dto.request.CreateLayoutRequest;
import org.homedecoration.dto.request.UpdateLayoutRequest;
import org.homedecoration.dto.response.HouseLayoutResponse;
import org.homedecoration.entity.HouseLayout;
import org.homedecoration.service.HouseLayoutService;
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

    @PostMapping("/create-draft")
    public ApiResponse<HouseLayoutResponse> createDraft(@RequestBody CreateLayoutRequest request) {
        return ApiResponse.success(
                HouseLayoutResponse.toDTO(houseLayoutService.createDraft(request))
        );
    }

    @PostMapping("/create-layout")
    public ApiResponse<HouseLayoutResponse> createLayout(
            @RequestBody @Valid CreateLayoutRequest request,
            HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").replace("Bearer ", "");
        request.setUserId(jwtUtil.getUserId(token));
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


}
