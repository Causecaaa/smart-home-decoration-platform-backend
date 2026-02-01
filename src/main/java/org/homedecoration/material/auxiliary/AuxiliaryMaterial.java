package org.homedecoration.material.auxiliary;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "auxiliary_material")
public class AuxiliaryMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long materialId;

    private String name;
    private String category; // CEMENT / PIPE / WIRE / ETC
    private String unit;     // kg/m/个/套
    private BigDecimal unitPrice;
    private String remark;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // getters & setters
}
