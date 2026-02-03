package org.homedecoration.construction.stage_assignment.repository;

import org.homedecoration.construction.stage_assignment.entity.StageAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StageAssignmentRepository extends JpaRepository<StageAssignment, Long> {

    List<StageAssignment> findByStageId(Long stageId);

    List<StageAssignment> findByWorkerId(Long workerId);
}
