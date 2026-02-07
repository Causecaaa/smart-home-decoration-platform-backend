package org.homedecoration.stage.stage.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.house.dto.response.HouseMaterialSummaryResponse;
import org.homedecoration.stage.stage.dto.request.StageUpdateRequest;
import org.homedecoration.stage.stage.dto.response.HouseStageMaterialsResponse;
import org.homedecoration.stage.stage.dto.response.HouseStageResponse;
import org.homedecoration.stage.stage.dto.response.StageDetailResponse;
import org.homedecoration.stage.stage.entity.Stage;
import org.homedecoration.stage.stage.service.GanttChartService;
import org.homedecoration.stage.stage.service.StageService;
import org.homedecoration.stage.assignment.dto.request.InviteWorkersRequest;
import org.homedecoration.stage.assignment.dto.response.StageAssignmentResponse;
import org.homedecoration.stage.assignment.service.StageAssignmentService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/stage")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;
    private final JwtUtil jwtUtil;
    private final GanttChartService ganttChartService;
    private final StageAssignmentService stageAssignmentService;

    @GetMapping("/{houseId}/getMaterials")
    public ApiResponse<HouseMaterialSummaryResponse> getMaterials(
            @PathVariable Long houseId
    ){
        return ApiResponse.success(stageService.calculateHouseMaterials(houseId));
    }

    @GetMapping("/{houseId}")
    public ApiResponse<HouseStageResponse> getGanttData(
            @PathVariable Long houseId,
            HttpServletRequest request) {

        Long userId = jwtUtil.getUserId(request);
        // 权限校验可以在 GanttChartService 内或者这里做
        return ApiResponse.success(ganttChartService.getGanttData(houseId));
    }


    @GetMapping("/{houseId}/{order}")
    public ApiResponse<StageDetailResponse> getStageDetail(
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

    @PostMapping("/{stageId}/update")
    public ApiResponse<Void> updateStage(
            @PathVariable Long stageId,
            @RequestBody StageUpdateRequest stageUpdateRequest,
            HttpServletRequest request) {

        Long userId = jwtUtil.getUserId(request);

        ganttChartService.updateStage(userId, stageId, stageUpdateRequest);
        return ApiResponse.success(null);
    }

    @PostMapping("/{houseId}/{order}/start")
    public ApiResponse<Void> startStage(
            @PathVariable Long houseId,
            @PathVariable Integer order,
            HttpServletRequest request) {

        Long userId = jwtUtil.getUserId(request);

        stageService.StartStage(userId, houseId, order);
        return ApiResponse.success(null);
    }
    @PostMapping("/{stageId}/complete")
    public ApiResponse<Void> completeStage(
            @PathVariable Long stageId,
            HttpServletRequest request) {

        jwtUtil.getUserId(request);

        stageService.CompleteStage(stageId);
        return ApiResponse.success(null);
    }
    @PostMapping("/{houseId}/{order}/accept")
    public ApiResponse<Void> acceptStage(
            @PathVariable Long houseId,
            @PathVariable Integer order,
            HttpServletRequest request) {

        Long userId = jwtUtil.getUserId(request);

        stageService.AcceptStage(userId, houseId, order);
        return ApiResponse.success(null);
    }

    @PostMapping("/{stageId}/invite-workers")
    public ApiResponse<List<StageAssignmentResponse>> inviteWorkers(
            @PathVariable Long stageId,
            @RequestBody @Valid InviteWorkersRequest request
    ) {
        List<StageAssignmentResponse> responses = stageAssignmentService.inviteWorkers(stageId, request)
                .stream()
                .map(StageAssignmentResponse::toDTO)
                .toList();
        return ApiResponse.success(responses);
    }

    @GetMapping("/{stageId}/invites")
    public ApiResponse<List<StageAssignmentResponse>> listStageInvites(
            @PathVariable Long stageId
    ) {
        List<StageAssignmentResponse> responses = stageAssignmentService.listInvitesByStage(stageId)
                .stream()
                .map(StageAssignmentResponse::toDTO)
                .toList();
        return ApiResponse.success(responses);
    }

}
