package org.homedecoration.material.material;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "material")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")  // 指定数据库字段名
    private Long id;  // 驼峰命名

    private String category; // FLOOR / WALL / CEILING / CABINET
    private String type;     // 枚举类型值
    @Column(name = "display_name")  // 映射到数据库字段
    private String displayName;
    @Column(name = "unit_price")    // 映射到数据库字段
    private BigDecimal unitPrice;
    private String unit;     // 默认㎡
    private String remark;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

