package org.homedecoration.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @Size(min = 8, message = "密码长度至少为8位")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*[0-9!@#$%^&*()_+\\-=]).+$",
            message = "密码必须包含字母和数字"
    )
    private String password;

    @Size(min = 5, max = 50, message = "用户名长度应该在5-20个字符之间")
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Size(max = 20, min = 10, message = "电话长度应该在10-20个字符之间")
    @Column(name = "phone", length = 20)
    private String phone;
}
