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
    private SchemeRoomMaterial.FloorMaterialType floor_material;
    private BigDecimal floor_area;
    private String floor_notes;

    // 墙面
    @JsonDeserialize(using = MaterialTypeDeserializer.class)
    private SchemeRoomMaterial.WallMaterialType wall_material;
    private BigDecimal wall_area;
    private String wall_notes;

    // 顶面
    @JsonDeserialize(using = MaterialTypeDeserializer.class)
    private SchemeRoomMaterial.CeilingMaterialType ceiling_material;
    private BigDecimal ceiling_area;
    private String ceiling_notes;

    // 柜体（定制）
    @JsonDeserialize(using = MaterialTypeDeserializer.class)
    private SchemeRoomMaterial.CabinetMaterialType cabinet_material;
    private BigDecimal cabinet_area;
    private String cabinet_notes;

}

