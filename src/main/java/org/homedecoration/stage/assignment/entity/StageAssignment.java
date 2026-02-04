package org.homedecoration.stage.assignment.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.homedecoration.identity.worker.entity.Worker;
import org.homedecoration.stage.stage.entity.Stage;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stage_assignment")
public class StageAssignment {
    public enum AssignmentStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
    public enum AssignmentType{
        WORK,
        LEAVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关联 Stage
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id", insertable = false, updatable = false)
    private Stage stage;

    @Column(name = "stage_id", nullable = false)
    private Long stageId;

    // 关联 Worker
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", insertable = false, updatable = false)
    private Worker worker;

    @Column(name = "worker_id", nullable = false)
    private Long workerId;

    @Column(name = "expected_start_at", nullable = false)
    private LocalDateTime expectedStartAt;

    @Column(name = "expected_end_at")
    private LocalDateTime expectedEndAt;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AssignmentStatus status = AssignmentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_type", nullable = false, length = 20)
    private AssignmentType type = AssignmentType.WORK;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
