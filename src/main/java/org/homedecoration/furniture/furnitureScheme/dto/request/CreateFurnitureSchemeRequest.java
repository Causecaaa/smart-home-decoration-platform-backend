package org.homedecoration.furniture.furnitureScheme.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.homedecoration.furniture.SchemeRoomMaterial.entity.SchemeRoomMaterial;

import java.math.BigDecimal;

@Data
public class CreateFurnitureSchemeRequest {
    private Long roomId;

    // 地面
    @JsonDeserialize(using = MaterialTypeDeserializer.class)
    private SchemeRoomMaterial.FloorMaterialType floorMaterial;
    private BigDecimal floorArea;

    // 墙面
    @JsonDeserialize(using = MaterialTypeDeserializer.class)
    private SchemeRoomMaterial.WallMaterialType wallMaterial;
    private BigDecimal wallArea;

    // 顶面
    @JsonDeserialize(using = MaterialTypeDeserializer.class)
    private SchemeRoomMaterial.CeilingMaterialType ceilingMaterial;
    private BigDecimal ceilingArea;

    // 柜体（定制）
    @JsonDeserialize(using = MaterialTypeDeserializer.class)
    private SchemeRoomMaterial.CabinetMaterialType cabinetMaterial;
    private BigDecimal cabinetArea;

    private String remark;
}

