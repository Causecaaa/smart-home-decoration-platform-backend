package org.homedecoration.service;

import org.homedecoration.dto.request.CreateUserRequest;
import org.homedecoration.entity.User;
import org.homedecoration.repository.UserRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 查询全部用户
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // 根据ID查询
    public User findById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()) {
            return optionalUser.get();
        }
        else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }

    // 根据用户名查询
    public User findByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isPresent()) {
            return optionalUser.get();
        }
        else {
            throw new RuntimeException("User not found with username: " + username);
        }
    }


    // 新增用户
    public User save(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(User.Role.USER);
        user.setStatus((byte) 1);
        return userRepository.save(user);
    }

    // 删除用户
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    // 更新用户信息
    public User updateProfile(Long id, String username, String password, String phone) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with id: " + id);
        }

        User user = optionalUser.get();
        user.setUsername(username);
        user.setPassword(password); // 后续可以加加密逻辑
        user.setPhone(phone);
        user.setUpdatedAt(Instant.now());

        return userRepository.save(user);
    }


    public User updateStatus(Long id, Byte status) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setStatus(status);
            user.setUpdatedAt(Instant.now()); // 更新时间
            return userRepository.save(user);
        }
        else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }

    public User updateRole(Long id, User.Role role) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setRole(role);
            user.setUpdatedAt(Instant.now());
            return userRepository.save(user);
        }
        else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }
}
