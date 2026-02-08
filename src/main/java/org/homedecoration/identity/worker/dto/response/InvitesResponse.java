package org.homedecoration.identity.worker.dto.response;

import lombok.Data;
import org.homedecoration.house.dto.response.HouseResponse;
import org.homedecoration.identity.user.dto.response.UserResponse;
import org.homedecoration.stage.assignment.dto.response.StageAssignmentResponse;

@Data
public class InvitesResponse {
    UserResponse employer;
    HouseResponse house;
    StageAssignmentResponse assignment;
}
