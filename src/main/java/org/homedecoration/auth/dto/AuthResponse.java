package org.homedecoration.auth.dto;

import lombok.Data;
import org.homedecoration.identity.user.dto.response.UserResponse;
import org.homedecoration.identity.worker.dto.response.WorkerInfoResponse;

@Data
public class AuthResponse {
    private String token;
    private UserResponse user;
    private WorkerInfoResponse worker;

    public static AuthResponse toDTO(String token, UserResponse userResponse, WorkerInfoResponse workerInfoResponse) {
        AuthResponse dto = new AuthResponse();
        dto.setToken(token);
        dto.setUser(userResponse);
        dto.setWorker(workerInfoResponse);
        return dto;
    }
}
