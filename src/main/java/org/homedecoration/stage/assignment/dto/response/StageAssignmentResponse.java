package org.homedecoration.stage.assignment.dto.response;

import lombok.Data;
import org.homedecoration.stage.assignment.entity.StageAssignment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StageAssignmentResponse {
    private Long id;
    private Long stageId;
    private Long workerId;
    private BigDecimal dailyWage;
    private LocalDateTime expectedStartAt;
    private LocalDateTime expectedEndAt;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private StageAssignment.AssignmentStatus status;

    public static StageAssignmentResponse toDTO(StageAssignment assignment) {
        StageAssignmentResponse dto = new StageAssignmentResponse();
        dto.setId(assignment.getId());
        dto.setStageId(assignment.getStageId());
        dto.setWorkerId(assignment.getWorkerId());
        dto.setDailyWage(assignment.getDailyWage());
        dto.setExpectedStartAt(assignment.getExpectedStartAt());
        dto.setExpectedEndAt(assignment.getExpectedEndAt());
        dto.setStartAt(assignment.getStartAt());
        dto.setEndAt(assignment.getEndAt());
        dto.setStatus(assignment.getStatus());
        return dto;
    }
}
