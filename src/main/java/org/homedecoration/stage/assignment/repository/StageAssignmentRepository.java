package org.homedecoration.stage.assignment.repository;

import org.homedecoration.stage.assignment.entity.StageAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StageAssignmentRepository extends JpaRepository<StageAssignment, Long> {
    List<StageAssignment> findByStageId(Long stageId);

    List<StageAssignment> findByWorkerId(Long workerId);

    List<StageAssignment> findByWorkerIdAndStatusIn(Long userId, List<StageAssignment.AssignmentStatus> pending);

    List<StageAssignment> findByWorkerIdAndExpectedStartAtAfterAndStatusInOrderByExpectedStartAtAsc(
            Long workerId,
            java.time.LocalDateTime expectedStartAt,
            List<StageAssignment.AssignmentStatus> statuses
    );

    List<StageAssignment> findByWorkerIdAndStatusInAndExpectedStartAtBeforeAndExpectedEndAtAfter(
            Long workerId,
            List<StageAssignment.AssignmentStatus> statuses,
            java.time.LocalDateTime expectedStartAt,
            java.time.LocalDateTime expectedEndAt
    );

    boolean existsByStageId(Long id);
}
