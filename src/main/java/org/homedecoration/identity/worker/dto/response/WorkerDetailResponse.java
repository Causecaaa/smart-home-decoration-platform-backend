package org.homedecoration.identity.worker.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.identity.worker.entity.Worker;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class WorkerDetailResponse {

    private Long userId;
    private String username;
    private String avatarUrl;
    private String phone;
    private String email;

    private String realName;
    private String city;
    private Boolean isPlatformWorker;
    private Worker.WorkStatus workStatus;
    private BigDecimal internalScore;
    private BigDecimal rating;
    private Integer orderCount;
    private Integer complaintCount;
    private Boolean enabled;

    public static WorkerDetailResponse toDTO(Worker worker, User user) {
        WorkerDetailResponse dto = new WorkerDetailResponse();
        dto.setUserId(worker.getUserId());
        if (user != null) {
            dto.setUsername(user.getUsername());
            dto.setAvatarUrl(user.getAvatarUrl());
            dto.setPhone(user.getPhone());
            dto.setEmail(user.getEmail());
        }
        dto.setRealName(worker.getRealName());
        dto.setCity(worker.getCity());
        dto.setIsPlatformWorker(worker.getIsPlatformWorker());
        dto.setWorkStatus(worker.getWorkStatus());
        dto.setInternalScore(worker.getInternalScore());
        dto.setRating(worker.getRating());
        dto.setOrderCount(worker.getOrderCount());
        dto.setComplaintCount(worker.getComplaintCount());
        dto.setEnabled(worker.getEnabled());
        return dto;
    }
}
