package org.homedecoration.user.repository;

import jakarta.validation.constraints.Size;
import org.homedecoration.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<Object> findByPhone(@Size(max = 20, min = 10, message = "电话长度应该在10-20个字符之间") String phone);
}
