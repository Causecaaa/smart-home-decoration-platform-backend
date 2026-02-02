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
    public enum StageStatus {
        PENDING,     // 待开始
        IN_PROGRESS, // 进行中
        COMPLETED,   // 已完成
        ACCEPTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "house_id", nullable = false)
    private Long houseId;

    @Column(name = "stage_order", nullable = false)
    private Integer order;

    @Column(name = "stage_name", nullable = false, length = 50)
    private String stageName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StageStatus status;  // 改成枚举类型

    @Column(name = "main_worker_type", nullable = false, length = 20)
    private String mainWorkerType;

    @Column(name = "required_count", nullable = false)
    private Integer requiredCount;

    @Column(name = "expected_start_at")
    private LocalDateTime expectedStartAt;

    @Column(name = "estimated_day")
    private Integer estimatedDay;

    @Column(name = "start_at")
    private LocalDateTime start_at;

    @Column(name = "end_at")
    private LocalDateTime end_at;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
