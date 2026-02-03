package org.homedecoration.construction.stage_assignment.dto.response;

import lombok.Data;
import org.homedecoration.construction.stage_assignment.entity.StageAssignment;

import java.time.LocalDateTime;

@Data
public class StageAssignmentResponse {

    private Long id;

    private Long stageId;

    private Long workerId;

    private StageAssignment.AssignmentStatus status;

    private LocalDateTime assignedAt;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static StageAssignmentResponse toDTO(StageAssignment assignment) {
        StageAssignmentResponse dto = new StageAssignmentResponse();
        dto.setId(assignment.getId());
        dto.setStageId(assignment.getStageId());
        dto.setWorkerId(assignment.getWorkerId());
        dto.setStatus(assignment.getStatus());
        dto.setAssignedAt(assignment.getAssignedAt());
        dto.setStartAt(assignment.getStartAt());
        dto.setEndAt(assignment.getEndAt());
        dto.setCreatedAt(assignment.getCreatedAt());
        dto.setUpdatedAt(assignment.getUpdatedAt());
        return dto;
    }
}
