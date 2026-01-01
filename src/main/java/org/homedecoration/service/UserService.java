package org.homedecoration.service;

import org.homedecoration.dto.request.CreateUserRequest;
import org.homedecoration.dto.request.UpdateProfileRequest;
import org.homedecoration.entity.User;
import org.homedecoration.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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


    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = getById(userId);

        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(user);
    }

    public User updateStatus(Long id, User.Status status) {
        User user = getById(id);
        user.setStatus(status);
        return userRepository.save(user);
    }

    public User updateRole(Long id, User.Role role) {
        User user = getById(id);
        user.setRole(role);
        return userRepository.save(user);
    }


    public void deleteById(Long id) {
        getById(id);
        userRepository.deleteById(id);
    }
}
