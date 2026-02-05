package org.homedecoration.house.dto.response;

import lombok.Data;
import org.homedecoration.bill.entity.Bill;
import org.homedecoration.house.entity.House;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HouseQuotationResponse {

    private Long houseId;
    private House.DecorationType decorationType;
    private BigDecimal totalCost;                 // 主材总价 + 辅材总价 + 人工费
    private BigDecimal mainMaterialsCost;         // 主材总价
    private BigDecimal auxiliaryMaterialsCost;    // 全屋辅材总价
    private BigDecimal laborCost;                 // 人工费总价
    private Long billId;
    private Bill.PayStatus payStatus;
    private List<AuxiliaryMaterialItem> auxiliaryMaterials;  // 辅材明细列表
    private List<RoomQuotation> rooms;            // 房间主材明细列表

    @Data
    public static class RoomQuotation {
        private Long roomId;
        private String roomName;
        private BigDecimal totalCost;
        private MainMaterials mainMaterials;
    }

    @Data
    public static class MainMaterials {
        private MaterialDetail floor;
        private MaterialDetail wall;
        private MaterialDetail ceiling;
        private MaterialDetail cabinet;
    }

    @Data
    public static class MaterialDetail {
        private String type;          // 枚举值，如 COMPOSITE_FLOOR
        private String displayName;   // 中文名
        private BigDecimal area;      // 面积㎡
        private BigDecimal unitPrice; // 单价
        private BigDecimal cost;      // 小项费用
        private String remark;
    }

    @Data
    public static class AuxiliaryMaterialItem {
        private String name;          // 辅材名称
        private String category;      // 类别：CEMENT / PIPE / WIRE / ETC
        private String unit;          // 单位：kg/m/个/套
        private BigDecimal unitPrice; // 单价
        private BigDecimal quantity;  // 数量
        private BigDecimal cost;      // 小计费用
        private String remark;        // 备注
    }
}
