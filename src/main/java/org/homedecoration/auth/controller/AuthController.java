package org.homedecoration.auth.controller;

import org.homedecoration.auth.dto.AuthResponse;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.homedecoration.identity.user.dto.response.UserResponse;
import org.homedecoration.identity.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/validate")
    public ApiResponse<AuthResponse> validateToken(HttpServletRequest request) {
        String token = jwtUtil.getToken(request);
        Long userId = jwtUtil.getUserId(request);
        UserResponse userResponse = UserResponse.toDTO(userService.getById(userId));
        return ApiResponse.success(
                AuthResponse.toDTO(token, userResponse)
        );
    }

}
