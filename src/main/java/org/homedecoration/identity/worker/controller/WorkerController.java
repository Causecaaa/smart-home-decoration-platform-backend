package org.homedecoration.identity.worker.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.identity.worker.dto.request.CreateWorkerRequest;
import org.homedecoration.identity.worker.dto.request.UpdateWorkerProfileRequest;
import org.homedecoration.identity.worker.dto.response.WorkerDetailResponse;
import org.homedecoration.identity.worker.entity.Worker;
import org.homedecoration.identity.worker.service.WorkerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/worker")
public class WorkerController {

    private final WorkerService workerService;
    private final JwtUtil jwtUtil;

    public WorkerController(WorkerService workerService, JwtUtil jwtUtil) {
        this.workerService = workerService;
        this.jwtUtil = jwtUtil;
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
