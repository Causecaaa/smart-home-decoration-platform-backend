package org.homedecoration.stage.stage.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class HouseStageMaterialsResponse {

    private List<StageMaterial> stages = new ArrayList<>();

    @Data
    public static class StageMaterial {
        private Integer stage;          // 1~8
        private String stageName;       // 阶段名称
        private List<MaterialInfo> mainMaterials = new ArrayList<>();
        private List<AuxMaterialInfo> auxiliaryMaterials = new ArrayList<>();
    }

    @Data
    public static class MaterialInfo {
        private String type;            // FLOOR, WALL, CEILING, CABINET
        private String displayName;     // 材料名
        private BigDecimal area;        // 面积
    }

    @Data
    public static class AuxMaterialInfo {
        private String name;
        private String category;
        private String unit;
        private BigDecimal quantity;
    }
}

