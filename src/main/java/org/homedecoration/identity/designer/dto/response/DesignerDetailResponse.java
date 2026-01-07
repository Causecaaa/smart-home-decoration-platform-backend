package org.homedecoration.identity.designer.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.homedecoration.identity.designer.entity.Designer;

@Getter
@Setter
public class DesignerDetailResponse {

    // ===== 用户基础信息 =====
    private Long userId;
    private String username;
    private String avatarUrl;

    // ===== 联系方式（详情页展示）=====
    private String phone;
    private String email;

    // ===== 设计师职业信息 =====
    private String realName;
    private Integer experienceYears;
    private String style;
    private String bio;
    private Double rating;      // 新增评分
    private Integer orderCount; // 新增累计接单数

    // ===== 平台状态字段 =====
    private String verifyStatus;
    private Boolean enabled;

    public static DesignerDetailResponse toDTO(Designer designer) {
        DesignerDetailResponse dto = new DesignerDetailResponse();

        // user
        dto.setUserId(designer.getUserId());
        dto.setUsername(designer.getUser().getUsername());
        dto.setAvatarUrl(designer.getUser().getAvatarUrl());
        dto.setPhone(designer.getUser().getPhone());
        dto.setEmail(designer.getUser().getEmail());

        // designer
        dto.setRealName(designer.getRealName());
        dto.setExperienceYears(designer.getExperienceYears());
        dto.setStyle(designer.getStyle());
        dto.setBio(designer.getBio());
        dto.setRating(designer.getRating());
        dto.setOrderCount(designer.getOrderCount());

        // platform
        dto.setVerifyStatus(designer.getVerifyStatus().name());
        dto.setEnabled(designer.getEnabled());

        return dto;
    }
}



