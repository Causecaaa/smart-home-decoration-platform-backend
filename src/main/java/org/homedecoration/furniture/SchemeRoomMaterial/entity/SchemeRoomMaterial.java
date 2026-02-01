package org.homedecoration.furniture.SchemeRoomMaterial.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "scheme_room_material")
public class SchemeRoomMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 方案ID */
    @Column(nullable = false)
    private Long schemeId;

    /** 房间ID */
    @Column(nullable = false)
    private Long roomId;

    /** 地面材料类型 */
    @Enumerated(EnumType.STRING)
    private FloorMaterialType floorMaterial;
    private BigDecimal floorArea;

    /** 墙面材料类型 */
    @Enumerated(EnumType.STRING)
    private WallMaterialType wallMaterial;
    private BigDecimal wallArea;

    /** 顶面材料类型 */
    @Enumerated(EnumType.STRING)
    private CeilingMaterialType ceilingMaterial;
    private BigDecimal ceilingArea;

    /** 柜体材料类型 */
    @Enumerated(EnumType.STRING)
    private CabinetMaterialType cabinetMaterial;
    private BigDecimal cabinetArea;

    /** 设计备注 */
    private String remark;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 枚举类型定义（也可以放在单独的文件中）
    @Getter
    public enum FloorMaterialType {
        TILES("瓷砖"),
        WOOD_FLOOR("木地板"),
        COMPOSITE_FLOOR("复合地板"),
        STONE("石材"),
        CARPET("地毯"),
        CEMENT_SELF_LEVELING("水泥自流平");

        private final String displayName;

        FloorMaterialType(String displayName) {
            this.displayName = displayName;
        }

        public static FloorMaterialType fromDisplayName(String displayName) {
            for (FloorMaterialType type : FloorMaterialType.values()) {
                if (type.displayName.equals(displayName)) {
                    return type;
                }
            }
            return null;
        }
    }

    @Getter
    public enum WallMaterialType {
        EMULSION_PAINT("乳胶漆"),
        WALLPAPER("壁纸"),
        DIATOM_MUDE("硅藻泥"),
        WALL_PANEL("护墙板"),
        TILES("瓷砖"),
        ARTISTIC_COATING("艺术涂料");

        private final String displayName;

        WallMaterialType(String displayName) {
            this.displayName = displayName;
        }

        public static WallMaterialType fromDisplayName(String displayName) {
            for (WallMaterialType type : WallMaterialType.values()) {
                if (type.displayName.equals(displayName)) {
                    return type;
                }
            }
            return null;
        }
    }

    @Getter
    public enum CeilingMaterialType {
        PLASTERBOARD_CEILING("石膏板吊顶"),
        INTEGRATED_CEILING("集成吊顶"),
        PVC_CEILING("PVC吊顶"),
        ALUMINUM_SCREW_CEILING("铝扣板吊顶"),
        ORIGINAL_TOP_BRUSH_WHITE("原顶刷白"),
        WOODEN_DECORATIVE_CEILING("木饰面吊顶");

        private final String displayName;

        CeilingMaterialType(String displayName) {
            this.displayName = displayName;
        }

        public static CeilingMaterialType fromDisplayName(String displayName) {
            for (CeilingMaterialType type : CeilingMaterialType.values()) {
                if (type.displayName.equals(displayName)) {
                    return type;
                }
            }
            return null;
        }
    }

    @Getter
    public enum CabinetMaterialType {
        SOLID_WOOD_PARTICLE_BOARD("实木颗粒板"),
        DENSITY_BOARD("密度板"),
        MULTI_LAYER_SOLID_WOOD_BOARD("多层实木板"),
        ECOLOGICAL_BOARD("生态板"),
        STAINLESS_STEEL("不锈钢"),
        ACRYLIC("亚克力");

        private final String displayName;

        CabinetMaterialType(String displayName) {
            this.displayName = displayName;
        }

        public static CabinetMaterialType fromDisplayName(String displayName) {
            for (CabinetMaterialType type : CabinetMaterialType.values()) {
                if (type.displayName.equals(displayName)) {
                    return type;
                }
            }
            return null;
        }
    }
}
