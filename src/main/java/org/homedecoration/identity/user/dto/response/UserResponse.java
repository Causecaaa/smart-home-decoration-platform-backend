package org.homedecoration.identity.user.dto.response;

import lombok.Data;
import org.homedecoration.identity.user.entity.User;

import java.time.Instant;

@Data
public class UserResponse {
    private Long userId;
    private String userName;
    private String email;
    private String phone;
    private User.Role role;
    private User.Status status;
    private String avatar_url;
    private Instant createdAt;
    private Instant updatedAt;

    public static UserResponse toDTO(User user) {
        UserResponse dto = new UserResponse();
        dto.setUserId(user.getId());
        dto.setUserName(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setAvatar_url(user.getAvatarUrl());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
