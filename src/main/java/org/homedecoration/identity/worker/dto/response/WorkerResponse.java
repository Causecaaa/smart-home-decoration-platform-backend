package org.homedecoration.identity.worker.dto.response;

import lombok.Data;
import org.homedecoration.identity.worker.worker_skill.entity.WorkerSkill;

import java.math.BigDecimal;

@Data
public class WorkerResponse {
    private Long userId;
    private String username;
    private String avatarUrl;
    private String phone;
    private String email;
    private String realName;

    private Integer page;
    private Integer size;
    private Integer totalPages;
    private Long totalElements;

    private BigDecimal rating;
    private WorkerSkill.Level level;

}