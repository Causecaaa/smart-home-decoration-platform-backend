package org.homedecoration.construction.stage.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.construction.stage.dto.response.HouseStageMaterialsResponse;
import org.homedecoration.construction.stage.service.StageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stage")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;
    private final JwtUtil jwtUtil;

    @GetMapping("/{houseId}")
    public ApiResponse<HouseStageMaterialsResponse> getHouseMaterialsByStage(
            @PathVariable Long houseId,
            HttpServletRequest  request) {
        Long userId = jwtUtil.getUserId(request);
        return ApiResponse.success(stageService.getHouseMaterialsByStage(houseId, userId));
    }

}
