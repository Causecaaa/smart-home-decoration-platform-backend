package org.homedecoration.furniture.furnitureScheme.dto.response;

import lombok.Data;
import org.homedecoration.furniture.SchemeRoomMaterial.entity.SchemeRoomMaterial;
import org.homedecoration.furniture.furnitureScheme.entity.FurnitureScheme;

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

    // ===== 材料信息（核心） =====

    private String floorMaterial;
    private Double floorArea;

    private String wallMaterial;
    private Double wallArea;

    private String ceilingMaterial;
    private Double ceilingArea;

    private String cabinetMaterial;
    private Double cabinetArea;

    private String remark;

    public static FurnitureSchemeResponse toDTO(
            FurnitureScheme scheme,
            SchemeRoomMaterial material
    ) {
        FurnitureSchemeResponse dto = new FurnitureSchemeResponse();

        // scheme 基本信息
        dto.setSchemeId(scheme.getId());
        dto.setRoomId(scheme.getRoom().getId());
        dto.setDesignerId(scheme.getDesignerId());
        dto.setSchemeStatus(scheme.getSchemeStatus().name());
        dto.setSchemeVersion(scheme.getSchemeVersion());
        dto.setImageUrl(scheme.getImageUrl());
        dto.setCreatedAt(scheme.getCreatedAt());
        dto.setUpdatedAt(scheme.getUpdatedAt());

        // material 信息（注意判空）
        if (material != null) {
            dto.setFloorMaterial(material.getFloorMaterial());
            dto.setFloorArea(toDouble(material.getFloorArea()));

            dto.setWallMaterial(material.getWallMaterial());
            dto.setWallArea(toDouble(material.getWallArea()));

            dto.setCeilingMaterial(material.getCeilingMaterial());
            dto.setCeilingArea(toDouble(material.getCeilingArea()));

            dto.setCabinetMaterial(material.getCabinetMaterial());
            dto.setCabinetArea(toDouble(material.getCabinetArea()));

            dto.setRemark(material.getRemark());
        }

        return dto;
    }

    private static Double toDouble(java.math.BigDecimal value) {
        return value == null ? null : value.doubleValue();
    }
}

