package org.homedecoration.identity.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @Size(min = 8, message = "新密码长度至少为8位")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*[0-9!@#$%^&*()_+\\-=]).+$",
            message = "新密码必须包含字母和数字"
    )
    private String newPassword;



}

