package org.homedecoration.identity.user.service;

import jakarta.transaction.Transactional;
import org.homedecoration.common.exception.BusinessException;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.identity.user.dto.request.ChangePasswordRequest;
import org.homedecoration.identity.user.dto.request.CreateUserRequest;
import org.homedecoration.identity.user.dto.request.UpdateProfileRequest;
import org.homedecoration.identity.user.dto.response.LoginResponse;
import org.homedecoration.identity.user.dto.response.UserResponse;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.identity.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class UserService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + id));
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found with username: " + username));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found with email: " + email));
    }


    public User createUser(CreateUserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("email already exists");
        }

        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("phone already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(User.Role.USER);
        user.setStatus(User.Status.ACTIVE);

        return userRepository.save(user);
    }


    public User login(String email, String password) {
        User user = getByEmail(email);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("password incorrect");
        }
        return user;
    }

    public LoginResponse loginAndGenerateToken(String email, String password) {
        User user = login(email, password);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(UserResponse.toDTO(user));

        return response;
    }



    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = getById(userId);

        user.setUsername(request.getUserName());
        user.setPhone(request.getPhone());

        return userRepository.save(user);
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = getById(userId);

        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 设置新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }


    public User updateStatus(Long id, User.Status status) {
        User user = getById(id);
        user.setStatus(status);
        return userRepository.save(user);
    }

    @Transactional
    public void updateRole(Long id, User.Role role) {
        User user = getById(id);
        user.setRole(role);
        // ❌ 不要 userRepository.save(user);
    }



    public void deleteById(Long id) {
        getById(id);
        userRepository.deleteById(id);
    }

    @Transactional
    public User uploadAvatar(Long userId, MultipartFile file) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("必须上传文件");
        }

        String originalName = file.getOriginalFilename();
        String filename = System.currentTimeMillis() + "_" + originalName;

        Path path = Paths.get(uploadDir + "/avatar", filename);
        Files.createDirectories(path.getParent());
        file.transferTo(path.toFile());

        user.setAvatarUrl("/uploads/avatar/" + filename);

        return userRepository.save(user);
    }
}
