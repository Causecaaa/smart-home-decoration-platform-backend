package org.homedecoration.construction.stage.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.construction.stage.dto.response.HouseStageMaterialsResponse;
import org.homedecoration.construction.stage.dto.response.HouseStageResponse;
import org.homedecoration.construction.stage.dto.response.StageDetailResponse;
import org.homedecoration.construction.stage.service.GanttChartService;
import org.homedecoration.construction.stage.service.StageService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/stage")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;
    private final JwtUtil jwtUtil;
    private final GanttChartService ganttChartService;

    @GetMapping("/{houseId}")
    public ApiResponse<HouseStageResponse> getGanttData(
            @PathVariable Long houseId,
            HttpServletRequest request) {

        Long userId = jwtUtil.getUserId(request);
        // 权限校验可以在 GanttChartService 内或者这里做
        return ApiResponse.success(ganttChartService.getGanttData(houseId));
    }


    @GetMapping("/{houseId}/{order}")
    public ApiResponse<StageDetailResponse.StageInfo> getStageDetail(
            @PathVariable Long houseId,
            @PathVariable Integer order,
            HttpServletRequest request) {
        Long userId = jwtUtil.getUserId(request);
        return ApiResponse.success(stageService.getStageDetail(houseId, userId, order));
    }



    @GetMapping("/{houseId}/get")
    public ApiResponse<HouseStageMaterialsResponse> getHouseMaterialsByStage(
            @PathVariable Long houseId,
            HttpServletRequest  request) {
        Long userId = jwtUtil.getUserId(request);
        return ApiResponse.success(stageService.getHouseMaterialsByStage(houseId, userId));
    }

    /**
     * 更新某阶段预期开始时间
     * expectedStartAt 格式：yyyy-MM-dd
     */
    @PostMapping("/{houseId}/{order}/schedule")
    public ApiResponse<Void> updateExpectedStartAt(
            @PathVariable Long houseId,
            @PathVariable Integer order,
            @RequestParam String expectedStartAt,
            HttpServletRequest request) {

        Long userId = jwtUtil.getUserId(request);
        LocalDateTime start;
        try {
            start = LocalDateTime.parse(expectedStartAt + "T00:00:00");
        } catch (DateTimeParseException e) {
            throw new RuntimeException("日期格式错误，应为 yyyy-MM-dd");
        }

        ganttChartService.updateExpectedStartAt(houseId, order, start);
        return ApiResponse.success(null);
    }

}
