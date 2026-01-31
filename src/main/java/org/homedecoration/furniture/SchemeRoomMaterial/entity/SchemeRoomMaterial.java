package org.homedecoration.furniture.SchemeRoomMaterial.entity;

import jakarta.persistence.*;
import lombok.Data;

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

    /** 地面 */
    private String floorMaterial;
    private BigDecimal floorArea;

    /** 墙面 */
    private String wallMaterial;
    private BigDecimal wallArea;

    /** 顶面 */
    private String ceilingMaterial;
    private BigDecimal ceilingArea;

    /** 柜体（定制类） */
    private String cabinetMaterial;
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
}