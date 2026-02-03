package org.homedecoration.identity.worker.worker_skill.dto.request;

import lombok.Data;
import org.homedecoration.identity.worker.worker_skill.repository.WorkerSkill;

@Data
public class UpdateWorkerSkillRequest {

    private WorkerSkill.WorkerType workerType;

    private WorkerSkill.Level level;
}
