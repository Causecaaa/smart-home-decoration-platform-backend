package org.homedecoration.stage.assignment.service;

import lombok.RequiredArgsConstructor;
import org.homedecoration.house.entity.House;
import org.homedecoration.house.service.HouseService;
import org.homedecoration.identity.worker.entity.Worker;
import org.homedecoration.identity.worker.service.WorkerService;
import org.homedecoration.stage.assignment.dto.request.CreateStageAssignmentRequest;
import org.homedecoration.stage.assignment.dto.request.UpdateStageAssignmentRequest;
import org.homedecoration.stage.assignment.entity.StageAssignment;
import org.homedecoration.stage.assignment.repository.StageAssignmentRepository;
import org.homedecoration.stage.stage.entity.Stage;
import org.homedecoration.stage.stage.service.StageService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StageAssignmentService {
    private final StageAssignmentRepository stageAssignmentRepository;
    private final StageService stageService;
    private final WorkerService workerService;
    private final HouseService houseService;

    public StageAssignment createAssignment(CreateStageAssignmentRequest request) {
        if (request.getStageId() == null || request.getWorkerId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "阶段ID和工人ID不能为空");
        }
        if (request.getExpectedStartAt() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "预计开始时间不能为空");
        }

        StageAssignment assignment = new StageAssignment();
        assignment.setStageId(request.getStageId());
        assignment.setWorkerId(request.getWorkerId());
        assignment.setExpectedStartAt(request.getExpectedStartAt());
        assignment.setExpectedEndAt(request.getExpectedEndAt());
        if (request.getStatus() != null) {
            assignment.setStatus(request.getStatus());
        }

        return stageAssignmentRepository.save(assignment);
    }

    public StageAssignment getAssignment(Long assignmentId) {
        return stageAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "派工记录不存在"));
    }

    public List<StageAssignment> listByStageId(Long stageId) {
        return stageAssignmentRepository.findByStageId(stageId);
    }

    public List<StageAssignment> listByWorkerId(Long workerId) {
        return stageAssignmentRepository.findByWorkerId(workerId);
    }

    public StageAssignment updateAssignment(Long assignmentId, UpdateStageAssignmentRequest request) {
        StageAssignment assignment = getAssignment(assignmentId);

        if (request.getStageId() != null) {
            assignment.setStageId(request.getStageId());
        }
        if (request.getWorkerId() != null) {
            assignment.setWorkerId(request.getWorkerId());
        }
        if (request.getExpectedStartAt() != null) {
            assignment.setExpectedStartAt(request.getExpectedStartAt());
        }
        if (request.getExpectedEndAt() != null) {
            assignment.setExpectedEndAt(request.getExpectedEndAt());
        }
        if (request.getStartAt() != null) {
            assignment.setStartAt(request.getStartAt());
        }
        if (request.getEndAt() != null) {
            assignment.setEndAt(request.getEndAt());
        }
        if (request.getStatus() != null) {
            assignment.setStatus(request.getStatus());
        }

        return stageAssignmentRepository.save(assignment);
    }

    public void deleteAssignment(Long assignmentId) {
        StageAssignment assignment = getAssignment(assignmentId);
        stageAssignmentRepository.delete(assignment);
    }

    public void updateAssignmentsForStage(Long stageId, LocalDateTime newStart) {
        List<StageAssignment> assignments = stageAssignmentRepository.findByStageId(stageId);
        Stage stage = stageService.getStage(stageId);
        House house = houseService.getHouseById(stage.getHouseId());

        // ---------- 1️⃣ 如果还没有派工记录，就需要先创建 ----------
        if (assignments.isEmpty()) {
            // 找到可用工人
            List<Worker> availableWorkers = workerService.findAvailableWorkers(
                    stage.getMainWorkerType(),
                    stage.getRequiredCount(),
                    house.getCity(),
                    newStart,
                    newStart.plusDays(stage.getEstimatedDay())
            );

            if (availableWorkers.size() < stage.getRequiredCount()) {
                throw new RuntimeException("没有足够可用工人来派工");
            }

            // 创建派工记录
            for (int i = 0; i < stage.getRequiredCount(); i++) {
                Worker w = availableWorkers.get(i);
                StageAssignment assignment = new StageAssignment();
                assignment.setStageId(stageId);
                assignment.setWorkerId(w.getUserId());
                assignment.setExpectedStartAt(newStart);
                assignment.setExpectedEndAt(newStart.plusDays(stage.getEstimatedDay()));
                assignment.setStatus(StageAssignment.AssignmentStatus.PENDING);

                stageAssignmentRepository.save(assignment);
            }

        } else {
            // ---------- 2️⃣ 更新已有 assignment ----------
            for (StageAssignment a : assignments) {
                a.setExpectedStartAt(newStart);
                a.setExpectedEndAt(newStart.plusDays(stage.getEstimatedDay()));
            }
            stageAssignmentRepository.saveAll(assignments);
        }
    }



}
