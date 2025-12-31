package org.homedecoration.controller;

import jakarta.validation.Valid;
import org.homedecoration.dto.request.CreateUserRequest;
import org.homedecoration.dto.response.UserResponse;
import org.homedecoration.entity.User;
import org.homedecoration.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 查询全部用户
    @GetMapping("/get-all")
    public List<UserResponse> getAllUsers() {
        return userService.findAll()
                .stream()
                .map(UserResponse::toDTO)
                .toList();
    }


    // 根据用户名查询
    @GetMapping("/username/{username}")
    public UserResponse getByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return UserResponse.toDTO(user);
    }


    // 查询单个用户（按 ID）
    @GetMapping("/id/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return UserResponse.toDTO(userService.findById(id));
    }

    // 新增用户
    @PostMapping("/create")
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return UserResponse.toDTO(userService.save(request));
    }

    @PutMapping("/update-profile/{id}")
    public User updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody User userRequest) {
        return userService.updateProfile(
                id,
                userRequest.getUsername(),
                userRequest.getPassword(),
                userRequest.getPhone()
        );
    }


    // 更新用户信息
    @PutMapping("/update/{id}")
    public UserResponse updateUser(@PathVariable Long id, @Valid @RequestBody CreateUserRequest request) {
        return UserResponse.toDTO(userService.save(request));
    }

    // 删除用户
    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }

    // 修改用户状态（启用/禁用）
    @PatchMapping("/update-status/{id}")
    public UserResponse updateUserStatus(@PathVariable Long id, @Valid @RequestParam Byte status) {
        return UserResponse.toDTO(userService.updateStatus(id, status));
    }

    // 修改用户角色
    @PatchMapping("/update-role/{id}")
    public UserResponse updateUserRole(@PathVariable Long id, @RequestParam User.Role role) {
        return UserResponse.toDTO(userService.updateRole(id, role));
    }

}
