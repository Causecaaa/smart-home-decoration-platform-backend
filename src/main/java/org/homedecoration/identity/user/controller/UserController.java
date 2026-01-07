package org.homedecoration.identity.user.controller;

import jakarta.validation.Valid;
import org.homedecoration.common.response.ApiResponse;
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

    public UserController(UserService userService) {
        this.userService = userService;
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
    @GetMapping("/id/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        return ApiResponse.success(
                UserResponse.toDTO(userService.getById(id))
        );
    }

    @PostMapping("/{id}/upload-avatar")
    public ApiResponse<UserResponse> uploadAvatar(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return ApiResponse.success(
                UserResponse.toDTO(userService.uploadAvatar(id, file))
        );
    }



    @PutMapping("/{id}/update-profile")
    public ApiResponse<UserResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.success(
                UserResponse.toDTO(userService.updateProfile(id, request))
        );
    }


    // 删除用户
    @DeleteMapping("/{id}/delete")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ApiResponse.success(null);
    }

    // 修改用户状态（启用/禁用）
    @PatchMapping("/{id}/update-status")
    public ApiResponse<UserResponse> updateUserStatus(@PathVariable Long id, @Valid @RequestParam User.Status status) {
        return ApiResponse.success(
                UserResponse.toDTO(userService.updateStatus(id, status))
        );
    }

    // 修改用户角色
    @PatchMapping("/{id}/update-role")
    public ApiResponse<UserResponse> updateUserRole(@PathVariable Long id, @Valid @RequestParam User.Role role) {
        return ApiResponse.success(
                UserResponse.toDTO(userService.updateRole(id, role))
        );
    }

}
