package org.homedecoration.identity.worker.worker_skill.dto.response;

import lombok.Data;
import org.homedecoration.identity.worker.worker_skill.repository.WorkerSkill;

import java.time.LocalDateTime;

@Data
public class WorkerSkillResponse {

    private Long id;

    private Long workerId;

    private WorkerSkill.WorkerType workerType;

    private WorkerSkill.Level level;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static WorkerSkillResponse toDTO(WorkerSkill skill) {
        WorkerSkillResponse dto = new WorkerSkillResponse();
        dto.setId(skill.getId());
        dto.setWorkerId(skill.getWorkerId());
        dto.setWorkerType(skill.getWorkerType());
        dto.setLevel(skill.getLevel());
        dto.setCreatedAt(skill.getCreatedAt());
        dto.setUpdatedAt(skill.getUpdatedAt());
        return dto;
    }
}
