package org.homedecoration.construction.stage.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "stage")
public class Stage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "house_id", nullable = false)
    private Long houseId;

    @Column(name = "stage_name", nullable = false, length = 50)
    private String stageName;

    @Column(name = "order_no", nullable = false)
    private Integer orderNo;

    @Column(name = "main_worker_type", nullable = false, length = 20)
    private String mainWorkerType;

    @Column(name = "required_count", nullable = false)
    private Integer requiredCount = 1;

    @Column(name = "estimated_day")
    private Integer estimatedDay;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
