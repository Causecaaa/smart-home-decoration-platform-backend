package org.homedecoration.stage.assignment.dto.request;

import lombok.Data;
import org.homedecoration.stage.assignment.entity.StageAssignment;

import java.time.LocalDateTime;

@Data
public class CreateStageAssignmentRequest {
    private Long stageId;
    private Long workerId;
    private LocalDateTime expectedStartAt;
    private LocalDateTime expectedEndAt;

    private StageAssignment.AssignmentStatus status;
//    AssignmentStatus {
//        PENDING,
//        IN_PROGRESS,
//        COMPLETED,
//        CANCELLED
//    }
}
