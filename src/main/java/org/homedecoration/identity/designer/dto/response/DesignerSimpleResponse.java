package org.homedecoration.identity.designer.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.homedecoration.identity.designer.entity.Designer;

@Getter
@Setter
public class DesignerSimpleResponse {

    private Long userId;

    private String name;

    private String avatar;

    private String style;

    private Integer experienceYears;

    private String shortBio;

    private Double rating;      // 新增评分

    private Integer orderCount; // 新增累计接单数

    public static DesignerSimpleResponse toDTO(Designer designer) {
        DesignerSimpleResponse dto = new DesignerSimpleResponse();
        dto.setUserId(designer.getUserId());
        dto.setName(designer.getRealName());
        dto.setAvatar(designer.getUser().getAvatarUrl());
        dto.setStyle(designer.getStyle());
        dto.setExperienceYears(designer.getExperienceYears());
        dto.setShortBio(designer.getBio());

        // 新增
        dto.setRating(designer.getRating());
        dto.setOrderCount(designer.getOrderCount());

        return dto;
    }
}
