package org.homedecoration.identity.worker.dto.response;

import lombok.Data;

@Data
public class WorkerSimpleResponse {
    private Long userId;
    private String username;
    private String avatarUrl;
    private String phone;
    private String email;

    private String realName;

}
