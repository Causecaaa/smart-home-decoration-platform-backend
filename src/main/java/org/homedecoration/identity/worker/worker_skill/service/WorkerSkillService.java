package org.homedecoration.identity.worker.worker_skill.service;

import lombok.RequiredArgsConstructor;
import org.homedecoration.identity.worker.repository.WorkerRepository;
import org.homedecoration.identity.worker.worker_skill.dto.request.CreateWorkerSkillRequest;
import org.homedecoration.identity.worker.worker_skill.dto.request.UpdateWorkerSkillRequest;
import org.homedecoration.identity.worker.worker_skill.repository.WorkerSkill;
import org.homedecoration.identity.worker.worker_skill.repository.WorkerSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkerSkillService {

    private final WorkerSkillRepository workerSkillRepository;
    private final WorkerRepository workerRepository;

    public WorkerSkill createSkill(Long workerId, CreateWorkerSkillRequest request) {
        if (!workerRepository.existsById(workerId)) {
            throw new IllegalArgumentException("工人不存在");
        }

        if (request.getWorkerType() == null) {
            throw new IllegalArgumentException("工种不能为空");
        }

        workerSkillRepository.findByWorkerIdAndWorkerType(workerId, request.getWorkerType())
                .ifPresent(skill -> {
                    throw new IllegalStateException("工人已存在该工种技能");
                });

        WorkerSkill skill = new WorkerSkill();
        skill.setWorkerId(workerId);
        skill.setWorkerType(request.getWorkerType());
        skill.setLevel(request.getLevel() == null ? WorkerSkill.Level.SKILLED : request.getLevel());

        return workerSkillRepository.save(skill);
    }

    @Transactional(readOnly = true)
    public List<WorkerSkill> listByWorkerId(Long workerId) {
        return workerSkillRepository.findByWorkerId(workerId);
    }

    public WorkerSkill updateSkill(Long skillId, UpdateWorkerSkillRequest request) {
        WorkerSkill skill = getSkillById(skillId);

        if (request.getWorkerType() != null && request.getWorkerType() != skill.getWorkerType()) {
            workerSkillRepository.findByWorkerIdAndWorkerType(skill.getWorkerId(), request.getWorkerType())
                    .ifPresent(existing -> {
                        throw new IllegalStateException("工人已存在该工种技能");
                    });
            skill.setWorkerType(request.getWorkerType());
        }

        if (request.getLevel() != null) {
            skill.setLevel(request.getLevel());
        }

        return workerSkillRepository.save(skill);
    }

    public void deleteSkill(Long skillId) {
        WorkerSkill skill = getSkillById(skillId);
        workerSkillRepository.delete(skill);
    }

    private WorkerSkill getSkillById(Long skillId) {
        return workerSkillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("工人技能不存在"));
    }
}
