package org.homedecoration.identity.worker.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.identity.worker.dto.request.CreateWorkerRequest;
import org.homedecoration.identity.worker.dto.request.LeaveRequest;
import org.homedecoration.identity.worker.dto.request.UpdateWorkerProfileRequest;
import org.homedecoration.identity.worker.dto.response.*;
import org.homedecoration.identity.worker.entity.Worker;
import org.homedecoration.identity.worker.service.WorkerService;
import org.homedecoration.identity.worker.worker_skill.entity.WorkerSkill;
import org.homedecoration.stage.assignment.dto.request.StageInviteResponseRequest;
import org.homedecoration.stage.assignment.dto.response.StageAssignmentResponse;
import org.homedecoration.stage.assignment.service.StageAssignmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/worker")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService workerService;
    private final JwtUtil jwtUtil;
    private final StageAssignmentService stageAssignmentService;



    @GetMapping("/available-workers")
    public ApiResponse<Page<WorkerResponse>> getAvailableWorkers(
            @RequestParam WorkerSkill.WorkerType mainWorkerType,
            @RequestParam WorkerSkill.Level level,
            @RequestParam String city,
            @RequestParam String expectedStartAtStr,
            @RequestParam String expectedEndAtStr,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        LocalDateTime expectedStartAt = LocalDateTime.parse(expectedStartAtStr);
        LocalDateTime expectedEndAt = LocalDateTime.parse(expectedEndAtStr);

        Page<WorkerResponse> availableWorkers = workerService.findAvailableWorkersForSelection(
                mainWorkerType,
                level,
                city,
                expectedStartAt,
                expectedEndAt,
                PageRequest.of(page, size)
        );

        return ApiResponse.success(availableWorkers);
    }

    @GetMapping("/labor-market")
    public ApiResponse<LaborMarketResponse> getLaborMarket(
            @RequestParam Long stageId,
            @RequestParam WorkerSkill.Level minLevel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        LaborMarketResponse response = workerService.getLaborMarketResponse(
                stageId,
                minLevel,
                PageRequest.of(page, size)
        );
        return ApiResponse.success(response);
    }


    @PostMapping("/apply")
    public ApiResponse<WorkerDetailResponse> applyWorker(
            HttpServletRequest request,
            @Valid @RequestBody CreateWorkerRequest createWorkerRequest) {
        Long userId = jwtUtil.getUserId(request);
        return ApiResponse.success(workerService.apply(userId, createWorkerRequest));
    }

    @GetMapping("/get")
    public ApiResponse<WorkerDetailResponse> getMyWorkerInfo(HttpServletRequest request) {
        Long userId = jwtUtil.getUserId(request);
        return ApiResponse.success(workerService.getDetailById(userId));
    }

    @PutMapping("/update")
    public ApiResponse<WorkerDetailResponse> updateMyProfile(
            HttpServletRequest request,
            @Valid @RequestBody UpdateWorkerProfileRequest body) {
        Long userId = jwtUtil.getUserId(request);
        return ApiResponse.success(workerService.updateProfile(userId, body));
    }

    @GetMapping("/assignments")
    public ApiResponse<List<StageAssignmentResponse>> listAssignments(HttpServletRequest request) {
        Long userId = jwtUtil.getUserId(request);
        List<StageAssignmentResponse> responses = stageAssignmentService
                .listAssignmentsByWorkerId(userId)
                .stream()
                .map(StageAssignmentResponse::toDTO)
                .toList();
        return ApiResponse.success(responses);
    }

    @GetMapping("/invites")
    public ApiResponse<List<InvitesResponse>> listInvites(HttpServletRequest request) {
        Long userId = jwtUtil.getUserId(request);
        List<InvitesResponse> responses = stageAssignmentService
                .listInvitesByWorkerId(userId)
                .stream()
                .map(stageAssignmentService::toInviteResponse)
                .toList();
        return ApiResponse.success(responses);
    }

    @PostMapping("/invites/{assignmentId}/respond")
    public ApiResponse<InvitesResponse> respondInvite(
            HttpServletRequest request,
            @PathVariable Long assignmentId,
            @Valid @RequestBody StageInviteResponseRequest body) {
        Long userId = jwtUtil.getUserId(request);
        return ApiResponse.success(
                stageAssignmentService.toInviteResponse(
                        stageAssignmentService.respondToInviteAsWorker(assignmentId, userId, body)
                )
        );
    }

    @GetMapping("/calendar")
    public ApiResponse<WorkerStageCalendarResponse> getStageCalendar(
            HttpServletRequest request,
            @RequestParam String month) {
        Long userId = jwtUtil.getUserId(request);
        YearMonth yearMonth = YearMonth.parse(month);
        return ApiResponse.success(stageAssignmentService.getWorkerStageCalendar(userId, yearMonth));
    }

    @PostMapping("/leave")
    public ApiResponse<List<StageAssignmentResponse>> requestLeave(
            HttpServletRequest request,
            @Valid @RequestBody LeaveRequest body) {
        Long userId = jwtUtil.getUserId(request);
        stageAssignmentService.applyLeaveForDate(userId, body);
        return ApiResponse.success(null);
    }

    @PostMapping("/leave/cancel")
    public ApiResponse<List<StageAssignmentResponse>> requestCancelLeave(
            HttpServletRequest request,
            @Valid @RequestBody LeaveRequest body) {
        Long userId = jwtUtil.getUserId(request);
        stageAssignmentService.cancelLeaveForDate(userId, body.getLeaveDate());
        return ApiResponse.success(null);
    }

    @GetMapping("/{workerId}/get")
    public ApiResponse<WorkerDetailResponse> getByWorkerId(@PathVariable Long workerId) {
        return ApiResponse.success(workerService.getDetailById(workerId));
    }

    @GetMapping("/list")
    public ApiResponse<List<WorkerDetailResponse>> listWorkers(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Worker.WorkStatus workStatus,
            @RequestParam(required = false) Boolean enabled) {
        return ApiResponse.success(workerService.list(city, workStatus, enabled));
    }

}
