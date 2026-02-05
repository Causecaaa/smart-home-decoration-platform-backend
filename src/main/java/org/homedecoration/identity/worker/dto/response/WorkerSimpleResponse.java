package org.homedecoration.identity.worker.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkerSimpleResponse {
    private Long userId;
    private String username;
    private String avatarUrl;
    private String phone;
    private String email;
    private String realName;

    private LocalDate expected_Start_at; // 新增
    private LocalDate expected_End_at; // 新增

}
