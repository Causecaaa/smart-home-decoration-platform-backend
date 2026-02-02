package org.homedecoration.identity.worker.worker_skill.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "worker_skill")
public class WorkerSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "worker_id", nullable = false)
    private Long workerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "worker_type", nullable = false)
    private WorkerType workerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", length = 20)
    private Level level;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 工种类别枚举
    public enum WorkerType {
        DEMOLITION,     // 拆除工
        ELECTRICAL,     // 电工
        TILING,         // 瓦工
        CARPENTRY,      // 木工
        PAINTING,       // 油漆工
        INSTALLATION,   // 安装工
        PLASTER         // 泥工
    }

    // 熟练度等级枚举
    public enum Level {
        JUNIOR,         // 初级
        SKILLED,        // 熟练
        MASTER          // 大师级
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
