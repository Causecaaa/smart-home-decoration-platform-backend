package org.homedecoration.stage.assignment.controller;

import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.stage.assignment.dto.request.CreateStageAssignmentRequest;
import org.homedecoration.stage.assignment.dto.request.UpdateStageAssignmentRequest;
import org.homedecoration.stage.assignment.dto.response.StageAssignmentResponse;
import org.homedecoration.stage.assignment.entity.StageAssignment;
import org.homedecoration.stage.assignment.service.StageAssignmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stage-assignment")
@RequiredArgsConstructor
public class StageAssignmentController {
    private final StageAssignmentService stageAssignmentService;

    @PostMapping("/create")
    public ApiResponse<StageAssignmentResponse> createAssignment(
            @RequestBody CreateStageAssignmentRequest request
    ) {
        StageAssignment assignment = stageAssignmentService.createAssignment(request);
        return ApiResponse.success(StageAssignmentResponse.toDTO(assignment));
    }

    @GetMapping("/{assignmentId}")
    public ApiResponse<StageAssignmentResponse> getAssignment(
            @PathVariable Long assignmentId
    ) {
        StageAssignment assignment = stageAssignmentService.getAssignment(assignmentId);
        return ApiResponse.success(StageAssignmentResponse.toDTO(assignment));
    }

    @GetMapping("/stage/{stageId}")
    public ApiResponse<List<StageAssignmentResponse>> listByStage(
            @PathVariable Long stageId
    ) {
        List<StageAssignmentResponse> responses = stageAssignmentService.listByStageId(stageId)
                .stream()
                .map(StageAssignmentResponse::toDTO)
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    @GetMapping("/worker/{workerId}")
    public ApiResponse<List<StageAssignmentResponse>> listByWorker(
            @PathVariable Long workerId
    ) {
        List<StageAssignmentResponse> responses = stageAssignmentService.listByWorkerId(workerId)
                .stream()
                .map(StageAssignmentResponse::toDTO)
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    @PutMapping("/{assignmentId}")
    public ApiResponse<StageAssignmentResponse> updateAssignment(
            @PathVariable Long assignmentId,
            @RequestBody UpdateStageAssignmentRequest request
    ) {
        StageAssignment assignment = stageAssignmentService.updateAssignment(assignmentId, request);
        return ApiResponse.success(StageAssignmentResponse.toDTO(assignment));
    }

    @DeleteMapping("/{assignmentId}")
    public ApiResponse<Void> deleteAssignment(
            @PathVariable Long assignmentId
    ) {
        stageAssignmentService.deleteAssignment(assignmentId);
        return ApiResponse.success(null);
    }
}
