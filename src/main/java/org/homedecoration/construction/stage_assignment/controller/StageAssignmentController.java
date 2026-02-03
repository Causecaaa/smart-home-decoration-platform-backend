package org.homedecoration.construction.stage_assignment.controller;

import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.construction.stage_assignment.dto.request.CreateStageAssignmentRequest;
import org.homedecoration.construction.stage_assignment.dto.request.UpdateStageAssignmentRequest;
import org.homedecoration.construction.stage_assignment.dto.response.StageAssignmentResponse;
import org.homedecoration.construction.stage_assignment.entity.StageAssignment;
import org.homedecoration.construction.stage_assignment.service.StageAssignmentService;
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
            @RequestBody CreateStageAssignmentRequest body) {
        StageAssignment assignment = stageAssignmentService.createAssignment(body);
        return ApiResponse.success(StageAssignmentResponse.toDTO(assignment));
    }

    @GetMapping("/{assignmentId}/get")
    public ApiResponse<StageAssignmentResponse> getAssignment(@PathVariable Long assignmentId) {
        return ApiResponse.success(StageAssignmentResponse.toDTO(stageAssignmentService.getById(assignmentId)));
    }

    @GetMapping("/stage/{stageId}/list")
    public ApiResponse<List<StageAssignmentResponse>> listByStage(@PathVariable Long stageId) {
        return ApiResponse.success(toResponseList(stageAssignmentService.listByStageId(stageId)));
    }

    @GetMapping("/worker/{workerId}/list")
    public ApiResponse<List<StageAssignmentResponse>> listByWorker(@PathVariable Long workerId) {
        return ApiResponse.success(toResponseList(stageAssignmentService.listByWorkerId(workerId)));
    }

    @PutMapping("/{assignmentId}/update")
    public ApiResponse<StageAssignmentResponse> updateAssignment(
            @PathVariable Long assignmentId,
            @RequestBody UpdateStageAssignmentRequest body) {
        StageAssignment assignment = stageAssignmentService.updateAssignment(assignmentId, body);
        return ApiResponse.success(StageAssignmentResponse.toDTO(assignment));
    }

    @DeleteMapping("/{assignmentId}")
    public ApiResponse<Void> deleteAssignment(@PathVariable Long assignmentId) {
        stageAssignmentService.deleteAssignment(assignmentId);
        return ApiResponse.success(null);
    }

    private List<StageAssignmentResponse> toResponseList(List<StageAssignment> assignments) {
        return assignments.stream()
                .map(StageAssignmentResponse::toDTO)
                .collect(Collectors.toList());
    }
}
