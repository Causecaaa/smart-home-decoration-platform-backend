package org.homedecoration.stage.assignment.dto.request;

import lombok.Data;
import org.homedecoration.stage.assignment.entity.StageAssignment;

import java.time.LocalDateTime;

@Data
public class UpdateStageAssignmentRequest {
    private Long stageId;
    private Long workerId;
    private LocalDateTime expectedStartAt;
    private LocalDateTime expectedEndAt;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private StageAssignment.AssignmentStatus status;
}
