package org.homedecoration.identity.user.dto.response;

import lombok.Data;
import org.homedecoration.identity.worker.dto.response.WorkerInfoResponse;

@Data
public class LoginResponse {
    private String token;
    private UserResponse user;
    private WorkerInfoResponse worker;
}
