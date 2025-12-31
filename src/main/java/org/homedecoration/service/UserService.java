package org.homedecoration.service;

import org.homedecoration.dto.request.CreateUserRequest;
import org.homedecoration.dto.request.UpdateProfileRequest;
import org.homedecoration.entity.User;
import org.homedecoration.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("用户不存在，id=" + id));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("用户不存在，username=" + username));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("用户不存在，email=" + email));
    }


    public User createUser(CreateUserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("邮箱已注册");
        }

        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("手机号已注册");
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
        User user = findByEmail(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }

        return user;
    }


    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findById(userId);

        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    public User updateStatus(Long id, User.Status status) {
        User user = findById(id);
        user.setStatus(status);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    public User updateRole(Long id, User.Role role) {
        User user = findById(id);
        user.setRole(role);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }


    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("用户不存在，id=" + id);
        }
        userRepository.deleteById(id);
    }
}
