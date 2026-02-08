package org.homedecoration.auth.controller;

import lombok.RequiredArgsConstructor;
import org.homedecoration.auth.dto.AuthResponse;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.homedecoration.identity.user.dto.response.UserResponse;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.identity.user.service.UserService;
import org.homedecoration.identity.worker.dto.response.WorkerInfoResponse;
import org.homedecoration.identity.worker.entity.Worker;
import org.homedecoration.identity.worker.repository.WorkerRepository;
import org.homedecoration.identity.worker.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final WorkerRepository workerRepository;

    @PostMapping("/validate")
    public ApiResponse<AuthResponse> validateToken(HttpServletRequest request) {
        String token = jwtUtil.getToken(request);
        Long userId = jwtUtil.getUserId(request);

        User user = userService.getById(userId);
        UserResponse userResponse = UserResponse.toDTO(user);

        WorkerInfoResponse workerInfoResponse = null;

        if (user.getRole() == User.Role.WORKER) {
            Worker worker = workerRepository.findById(userId).orElse(null);
            if (worker != null) {
                workerInfoResponse = WorkerInfoResponse.toDTO(worker);
            }
        }

        return ApiResponse.success(
                AuthResponse.toDTO(token, userResponse, workerInfoResponse)
        );
    }


}
