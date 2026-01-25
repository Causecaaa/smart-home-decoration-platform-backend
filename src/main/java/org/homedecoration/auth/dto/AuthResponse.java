package org.homedecoration.auth.dto;

import lombok.Data;
import org.homedecoration.identity.user.dto.response.UserResponse;

@Data
public class AuthResponse {
    private String token;
    private UserResponse user;

    public static AuthResponse toDTO(String token, UserResponse userResponse) {
        AuthResponse dto = new AuthResponse();
        dto.setToken(token);
        dto.setUser(userResponse);
        return dto;
    }
}
