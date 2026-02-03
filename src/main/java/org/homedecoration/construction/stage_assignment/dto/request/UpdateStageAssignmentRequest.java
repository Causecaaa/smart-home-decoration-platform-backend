package org.homedecoration.construction.stage_assignment.dto.request;

import lombok.Data;
import org.homedecoration.construction.stage_assignment.entity.StageAssignment;

import java.time.LocalDateTime;

@Data
public class UpdateStageAssignmentRequest {

    private StageAssignment.AssignmentStatus status;

    private LocalDateTime startAt;

    private LocalDateTime endAt;
}
