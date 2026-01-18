package org.homedecoration.furnitureScheme.dto.response;

import lombok.Data;
import org.homedecoration.furnitureScheme.entity.FurnitureScheme;

import java.time.Instant;

@Data
public class FurnitureSchemeResponse {

    private Long schemeId;
    private Long roomId;
    private Long designerId;
    private String schemeStatus;
    private Integer schemeVersion;
    private String imageUrl;
    private Instant createdAt;
    private Instant updatedAt;

    public static FurnitureSchemeResponse toDTO(FurnitureScheme scheme) {
        FurnitureSchemeResponse dto = new FurnitureSchemeResponse();
        dto.setSchemeId(scheme.getId());
        dto.setRoomId(scheme.getRoom().getId());
        dto.setDesignerId(scheme.getDesignerId());
        dto.setSchemeStatus(scheme.getSchemeStatus().name());
        dto.setSchemeVersion(scheme.getSchemeVersion());
        dto.setImageUrl(scheme.getImageUrl());
        dto.setCreatedAt(scheme.getCreatedAt());
        dto.setUpdatedAt(scheme.getUpdatedAt());
        return dto;
    }
}
