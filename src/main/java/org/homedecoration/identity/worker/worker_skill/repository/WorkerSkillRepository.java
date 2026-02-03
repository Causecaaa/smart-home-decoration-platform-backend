package org.homedecoration.identity.worker.worker_skill.repository;

import org.homedecoration.identity.worker.worker_skill.entity.WorkerSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerSkillRepository extends JpaRepository<WorkerSkill, Long> {

    List<WorkerSkill> findByWorkerId(Long workerId);

    Optional<WorkerSkill> findByWorkerIdAndWorkerType(Long workerId, WorkerSkill.WorkerType workerType);

    List<WorkerSkill> findByWorkerType(WorkerSkill.WorkerType workerType);
}
