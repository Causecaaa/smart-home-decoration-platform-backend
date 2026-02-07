package org.homedecoration.identity.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.identity.user.dto.request.ChangePasswordRequest;
import org.homedecoration.identity.user.dto.request.CreateUserRequest;
import org.homedecoration.identity.user.dto.request.LoginRequest;
import org.homedecoration.identity.user.dto.request.UpdateProfileRequest;
import org.homedecoration.identity.user.dto.response.LoginResponse;
import org.homedecoration.identity.user.dto.response.UserResponse;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.identity.user.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // 新增用户
    @PostMapping("/create")
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ApiResponse.success(
                UserResponse.toDTO(userService.createUser(request))
        );
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(
                userService.loginAndGenerateToken(request.getEmail(), request.getPassword())
        );
    }


    // 查询全部用户
    @GetMapping("/get-all")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<UserResponse> list = userService.getAll()
                .stream()
                .map(UserResponse::toDTO)
                .toList();
        return ApiResponse.success(list);
    }


    // 根据用户名查询
    @GetMapping("/username/{username}")
    public ApiResponse<UserResponse> getByUsername(@PathVariable String username) {
        return ApiResponse.success(
                UserResponse.toDTO(userService.getByUsername(username))
        );
    }


    // 查询单个用户（按 ID）
    @GetMapping("/userInfo")
    public ApiResponse<UserResponse> getUserById(HttpServletRequest httpRequest) {
        Long id = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(
                UserResponse.toDTO(userService.getById(id))
        );
    }

    @PostMapping("/upload-avatar")
    public ApiResponse<UserResponse> uploadAvatar(
            HttpServletRequest httpRequest,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        Long id = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(
                UserResponse.toDTO(userService.uploadAvatar(id, file))
        );
    }


    @PutMapping("/update-profile")
    public ApiResponse<UserResponse> updateProfile(
            HttpServletRequest httpRequest,
            @Valid @RequestBody UpdateProfileRequest request) {
        Long id = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(
                UserResponse.toDTO(userService.updateProfile(id, request))
        );
    }

    @PutMapping("/change-password")
    public ApiResponse<Void> changePassword(
            HttpServletRequest httpRequest,
            @Valid @RequestBody ChangePasswordRequest request) {
        Long id = jwtUtil.getUserId(httpRequest);
        userService.changePassword(id, request);
        return ApiResponse.success(null);
    }


    // 删除用户
    @DeleteMapping("/{id}/delete")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ApiResponse.success(null);
    }

    // 修改用户状态（启用/禁用）
    @PatchMapping("/{id}/update-status")
    public ApiResponse<UserResponse> updateUserStatus(@PathVariable Long id, @RequestParam User.Status status) {
        return ApiResponse.success(
                UserResponse.toDTO(userService.updateStatus(id, status))
        );
    }

    // 修改用户角色
    @PatchMapping("/{id}/update-role")
    public ApiResponse<Void> updateUserRole(@PathVariable Long id, @RequestParam User.Role role) {
        userService.updateRole(id, role);
        return ApiResponse.success(
                null
        );
    }

}
