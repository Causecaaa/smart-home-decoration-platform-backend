package org.homedecoration.furniture.furnitureScheme.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateFurnitureSchemeRequest {

    private Long roomId;

    // 地面
    private String floorMaterial;
    private BigDecimal floorArea;

    // 墙面
    private String wallMaterial;
    private BigDecimal wallArea;

    // 顶面
    private String ceilingMaterial;
    private BigDecimal ceilingArea;

    // 柜体（定制）
    private String cabinetMaterial;
    private BigDecimal cabinetArea;

    private String remark;
}
