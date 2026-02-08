package org.homedecoration.identity.worker.dto.response;

import lombok.Data;
import org.homedecoration.stage.assignment.entity.StageAssignment;

import java.time.LocalDate;

@Data
public class WorkerSimpleResponse {
    private Long userId;
    private String username;
    private String avatarUrl;
    private String phone;
    private String email;
    private String realName;

    private StageAssignment.AssignmentStatus status;
    private double daily_wage;
    private LocalDate expected_Start_at;
    private LocalDate expected_End_at;

}
