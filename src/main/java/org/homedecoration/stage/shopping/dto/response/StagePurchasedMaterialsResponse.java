package org.homedecoration.stage.shopping.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class StagePurchasedMaterialsResponse {

    private List<MainMaterial> mainMaterials = new ArrayList<>();
    private List<AuxMaterial> auxiliaryMaterials = new ArrayList<>();

    @Data
    public static class MainMaterial {
        private String type;         // FLOOR, WALL, CEILING, CABINET
        private String displayName;  // 商品名
        private String image_url;
        private String unit;
        private String brand;
        private String remark;       // 备注
        private BigDecimal unitPrice; // 单价
        private BigDecimal subtotal; // 小计
        private BigDecimal quantity; // 购买数量
    }

    @Data
    public static class AuxMaterial {
        private String name;         // 商品名
        private String category;     // 子类
        private String brand;
        private String unit;         // 单位
        private BigDecimal quantity; // 购买数量
        private BigDecimal unitPrice;// 单价
        private BigDecimal subtotal; // 小计
    }
}
