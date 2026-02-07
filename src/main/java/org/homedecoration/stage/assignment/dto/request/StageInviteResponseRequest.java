package org.homedecoration.stage.assignment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.homedecoration.stage.assignment.entity.StageAssignment;

@Data
public class StageInviteResponseRequest {
    @NotNull(message = "状态不能为空")
    private StageAssignment.AssignmentStatus status;
}
