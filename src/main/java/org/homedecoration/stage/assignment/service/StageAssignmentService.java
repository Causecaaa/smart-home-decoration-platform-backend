package org.homedecoration.stage.assignment.service;

import jakarta.transaction.Transactional;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public List<StageAssignment> listAssignmentsByWorkerId(Long workerId) {
        return stageAssignmentRepository.findByWorkerId(
                workerId
        );
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

    private Worker findReplacement(Stage stage,
                                   House house,
                                   StageAssignment assignment) {

        List<StageAssignment> stageAssignments =
                stageAssignmentRepository.findByStageId(stage.getId());

        Set<Long> excludedWorkers = new HashSet<>();
        for (StageAssignment sa : stageAssignments) {
            excludedWorkers.add(sa.getWorkerId());
        }

        Worker replacement = workerService.findReplacementWorker(
                stage.getMainWorkerType(),
                house.getCity(),
                assignment.getExpectedStartAt(),
                assignment.getExpectedEndAt(),
                excludedWorkers
        );

        if (replacement == null) {
            throw new RuntimeException("找不到可替换工人");
        }

        return replacement;
    }

    @Transactional
    public List<StageAssignment> applyLeaveForDate(Long workerId, LocalDate leaveDate) {

        LocalDateTime dayStart = leaveDate.atStartOfDay();
        LocalDateTime dayEnd   = dayStart.plusDays(1);

        // 1️⃣ 找到命中当天的 assignment
        List<StageAssignment> assignments =
                stageAssignmentRepository
                        .findByWorkerIdAndStatusInAndExpectedStartAtBeforeAndExpectedEndAtAfter(
                                workerId,
                                List.of(StageAssignment.AssignmentStatus.PENDING,
                                        StageAssignment.AssignmentStatus.IN_PROGRESS),
                                dayEnd,
                                dayStart
                        );

        if (assignments.isEmpty()) {
            // 没活也要占位，防止被排新活（可选）
            createLeaveAssignment(workerId, null, leaveDate);
            return null;
        }

        for (StageAssignment assignment : assignments) {

            Stage stage = stageService.getStage(assignment.getStageId());
            House house = houseService.getHouseById(stage.getHouseId());

            LocalDate start = assignment.getExpectedStartAt().toLocalDate();
            LocalDate end   = assignment.getExpectedEndAt().toLocalDate();

            // ========= 情况一：还没开始，直接整体换人 =========
            if (leaveDate.isBefore(start)) {

                Worker replacement = findReplacement(stage, house, assignment);
                assignment.setWorkerId(replacement.getUserId());
                stageAssignmentRepository.save(assignment);
                continue;
            }

            // ========= 情况二：已开始，必须拆 =========

            // ① 截断原 assignment（张三）
            assignment.setExpectedEndAt(leaveDate.minusDays(1).atStartOfDay());
            assignment.setStatus(StageAssignment.AssignmentStatus.IN_PROGRESS);
            stageAssignmentRepository.save(assignment);

            // ② 请假占位（张三）
            createLeaveAssignment(workerId, stage.getId(), leaveDate);

            // ③ 剩余天数找人接（李四）
            if (leaveDate.isBefore(end)) {

                Worker replacement = findReplacement(stage, house, assignment);

                StageAssignment newAssignment = new StageAssignment();
                newAssignment.setStageId(stage.getId());
                newAssignment.setWorkerId(replacement.getUserId());
                newAssignment.setExpectedStartAt(leaveDate.plusDays(1).atStartOfDay());
                newAssignment.setExpectedEndAt(end.atStartOfDay());
                newAssignment.setStatus(StageAssignment.AssignmentStatus.PENDING);

                stageAssignmentRepository.save(newAssignment);
            }
        }

        return listAssignmentsByWorkerId(workerId);
    }

    private void createLeaveAssignment(Long workerId,
                                       Long stageId,
                                       LocalDate leaveDate) {

        StageAssignment leave = new StageAssignment();

        leave.setWorkerId(workerId);
        leave.setStageId(stageId); // 可以为 null：纯占位
        leave.setExpectedStartAt(leaveDate.atStartOfDay());
        leave.setExpectedEndAt(leaveDate.plusDays(1).atStartOfDay());

        // 不参与施工，只占用时间
        leave.setStatus(StageAssignment.AssignmentStatus.CANCELLED);

        stageAssignmentRepository.save(leave);
    }



}
