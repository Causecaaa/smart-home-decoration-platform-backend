package org.homedecoration.house.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class HouseMaterialSummaryResponse {

    private Long houseId;

    // 主材：按类型汇总
    private Map<String, List<MaterialDetail>> mainMaterials = new LinkedHashMap<>();

    // 辅材：按名称汇总总量
    private List<AuxiliaryMaterialItem> auxiliaryMaterials = new ArrayList<>();

    @Data
    public static class MaterialDetail {
        private String type;          // 类型，例如 FLOOR / WALL / CEILING / CABINET
        private String displayName;   // 材料名称
        private BigDecimal area;      // 面积
    }

    @Data
    public static class AuxiliaryMaterialItem {
        private String name;
        private String category;
        private String unit;
        private BigDecimal quantity;
        private String remark;
    }
}

