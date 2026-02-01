package org.homedecoration.identity.worker.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "worker")
public class Worker {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "real_name", nullable = false, length = 50)
    private String realName;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Column(name = "is_platform_worker", nullable = false)
    private Boolean isPlatformWorker = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_status", nullable = false, length = 20)
    private WorkStatus workStatus = WorkStatus.IDLE;

    @Column(name = "internal_score", precision = 3, scale = 2)
    private BigDecimal internalScore = BigDecimal.valueOf(5.0);

    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.valueOf(5.0);


    @Column(name = "order_count")
    private Integer orderCount = 0;

    @Column(name = "complaint_count")
    private Integer complaintCount = 0;

    @Column(name = "enabled")
    private Boolean enabled = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum WorkStatus {
        IDLE,    // 空闲
        BUSY,    // 忙碌
        DISABLED // 禁用
    }
}
