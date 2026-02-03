package org.homedecoration.construction.stage_assignment.service;

import lombok.RequiredArgsConstructor;
import org.homedecoration.construction.stage.repository.StageRepository;
import org.homedecoration.construction.stage_assignment.dto.request.CreateStageAssignmentRequest;
import org.homedecoration.construction.stage_assignment.dto.request.UpdateStageAssignmentRequest;
import org.homedecoration.construction.stage_assignment.entity.StageAssignment;
import org.homedecoration.construction.stage_assignment.repository.StageAssignmentRepository;
import org.homedecoration.identity.worker.repository.WorkerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StageAssignmentService {

    private final StageAssignmentRepository stageAssignmentRepository;
    private final StageRepository stageRepository;
    private final WorkerRepository workerRepository;

    public StageAssignment createAssignment(CreateStageAssignmentRequest request) {
        if (request.getStageId() == null) {
            throw new IllegalArgumentException("阶段不能为空");
        }
        if (request.getWorkerId() == null) {
            throw new IllegalArgumentException("工人不能为空");
        }
        if (!stageRepository.existsById(request.getStageId())) {
            throw new IllegalArgumentException("阶段不存在");
        }
        if (!workerRepository.existsById(request.getWorkerId())) {
            throw new IllegalArgumentException("工人不存在");
        }

        StageAssignment assignment = new StageAssignment();
        assignment.setStageId(request.getStageId());
        assignment.setWorkerId(request.getWorkerId());
        assignment.setStatus(request.getStatus() == null
                ? StageAssignment.AssignmentStatus.PENDING
                : request.getStatus());
        assignment.setStartAt(request.getStartAt());
        assignment.setEndAt(request.getEndAt());

        return stageAssignmentRepository.save(assignment);
    }

    @Transactional(readOnly = true)
    public StageAssignment getById(Long assignmentId) {
        return stageAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("派工记录不存在"));
    }

    @Transactional(readOnly = true)
    public List<StageAssignment> listByStageId(Long stageId) {
        return stageAssignmentRepository.findByStageId(stageId);
    }

    @Transactional(readOnly = true)
    public List<StageAssignment> listByWorkerId(Long workerId) {
        return stageAssignmentRepository.findByWorkerId(workerId);
    }

    public StageAssignment updateAssignment(Long assignmentId, UpdateStageAssignmentRequest request) {
        StageAssignment assignment = getById(assignmentId);

        if (request.getStatus() != null) {
            assignment.setStatus(request.getStatus());
        }
        if (request.getStartAt() != null) {
            assignment.setStartAt(request.getStartAt());
        }
        if (request.getEndAt() != null) {
            assignment.setEndAt(request.getEndAt());
        }

        return stageAssignmentRepository.save(assignment);
    }

    public void deleteAssignment(Long assignmentId) {
        StageAssignment assignment = getById(assignmentId);
        stageAssignmentRepository.delete(assignment);
    }
}
