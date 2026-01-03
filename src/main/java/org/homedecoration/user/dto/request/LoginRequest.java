package org.homedecoration.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(min = 8, message = "密码长度至少为8位")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*[0-9!@#$%^&*()_+\\-=]).+$",
            message = "密码必须包含字母和数字"
    )
    private String password;

    // 构造函数
    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
