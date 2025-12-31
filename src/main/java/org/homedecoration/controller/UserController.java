package org.homedecoration.controller;

import jakarta.validation.Valid;
import org.homedecoration.entity.User;
import org.homedecoration.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 查询全部用户
    @GetMapping("/get-all")
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    // 根据用户名查询
    @GetMapping("/find-by-username/{username}")
    public Optional<User> getByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    // 查询单个用户（按 ID）
    @GetMapping("/get/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    // 新增用户
    @PostMapping("/create")
    public User createUser(@Valid @RequestBody User user) {
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return userService.save(user);
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
    public User updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        user.setId(id);
        user.setUpdatedAt(Instant.now());
        return userService.save(user);
    }

    // 删除用户
    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }

    // 修改用户状态（启用/禁用）
    @PatchMapping("/update-status/{id}")
    public User updateUserStatus(@PathVariable Long id,@Valid @RequestParam Byte status) {
        return userService.updateStatus(id, status);
    }

    // 修改用户角色
    @PatchMapping("/update-role/{id}")
    public User updateUserRole(@PathVariable Long id, @RequestParam User.Role role) {
        return userService.updateRole(id, role);
    }

}
