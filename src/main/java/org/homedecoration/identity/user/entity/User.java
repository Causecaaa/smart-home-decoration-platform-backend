package org.homedecoration.identity.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    public enum Role {
        USER,
        MERCHANT,
        DESIGNER,
        INSPECTOR,
        ADMIN
    }

    public enum Status {
        ACTIVE,
        INACTIVE,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(min = 5, max = 50, message = "用户名长度应该在5-20个字符之间")
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Size(min = 8, message = "密码长度至少为8位")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*[0-9!@#$%^&*()_+\\-=]).+$",
            message = "密码必须包含字母和数字"
    )
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 20, min = 10, message = "电话长度应该在10-20个字符之间")
    @Column(name = "phone", length = 20)
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}