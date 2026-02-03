package org.homedecoration.identity.worker.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.identity.worker.dto.request.CreateWorkerRequest;
import org.homedecoration.identity.worker.dto.request.UpdateWorkerProfileRequest;
import org.homedecoration.identity.worker.dto.response.WorkerDetailResponse;
import org.homedecoration.identity.worker.entity.Worker;
import org.homedecoration.identity.worker.service.WorkerService;
import org.homedecoration.identity.worker.worker_skill.entity.WorkerSkill;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/worker")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService workerService;
    private final JwtUtil jwtUtil;

    @GetMapping("/available-workers")
    public ApiResponse<List<Worker>> getAvailableWorkers(
            @RequestParam WorkerSkill.WorkerType mainWorkerType,
            @RequestParam Integer requiredCount,
            @RequestParam String city,
            @RequestParam String expectedStartAtStr,
            @RequestParam String expectedEndAtStr) {

        // 解析时间参数
        LocalDateTime expectedStartAt = LocalDateTime.parse(expectedStartAtStr);
        LocalDateTime expectedEndAt = LocalDateTime.parse(expectedEndAtStr);

        // 调用服务方法
        List<Worker> availableWorkers = workerService.findAvailableWorkers(
                mainWorkerType, requiredCount, city, expectedStartAt, expectedEndAt);


        return ApiResponse.success(availableWorkers);
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
