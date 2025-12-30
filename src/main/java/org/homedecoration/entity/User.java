package org.homedecoration.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
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

    @Pattern(
            regexp = "USER|MERCHANT|DESIGNER|INSPECTOR|ADMIN",
            message = "角色必须是 USER、MERCHANT、DESIGNER、INSPECTOR 或 ADMIN 之一"
    )
    @Column(name = "role", nullable = false, length = 20)
    private String role;

    @Min(value = 0,message = "status 不能小于0")
    @Max(value = 2,message = "status 不能超过2")
    @ColumnDefault("1")
    @Column(name = "status", nullable = false)
    private Byte status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

}