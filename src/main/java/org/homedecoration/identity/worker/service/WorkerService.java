package org.homedecoration.identity.worker.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.identity.user.repository.UserRepository;
import org.homedecoration.identity.user.service.UserService;
import org.homedecoration.identity.worker.dto.request.CreateWorkerRequest;
import org.homedecoration.identity.worker.dto.request.UpdateWorkerProfileRequest;
import org.homedecoration.identity.worker.dto.response.WorkerDetailResponse;
import org.homedecoration.identity.worker.entity.Worker;
import org.homedecoration.identity.worker.repository.WorkerRepository;
import org.homedecoration.identity.worker.worker_skill.entity.WorkerSkill;
import org.homedecoration.identity.worker.worker_skill.repository.WorkerSkillRepository;
import org.homedecoration.stage.assignment.entity.StageAssignment;
import org.homedecoration.stage.assignment.repository.StageAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final UserService userService;
    private final StageAssignmentRepository stageAssignmentRepository;
    private final WorkerSkillRepository workerSkillRepository;

    public WorkerDetailResponse apply(Long userId, @Valid CreateWorkerRequest request) {
        if (workerRepository.existsById(userId)) {
            throw new IllegalStateException("用户已是工人");
        }

        User user = userService.getById(userId);

        Worker worker = new Worker();
        worker.setUserId(userId);
        worker.setRealName(request.getRealName());
        worker.setCity(request.getCity());
        worker.setIsPlatformWorker(request.getIsPlatformWorker());
        worker.setWorkStatus(
                request.getWorkStatus() == null
                        ? Worker.WorkStatus.IDLE
                        : request.getWorkStatus()
        );

        workerRepository.save(worker);

        userService.updateRole(userId, User.Role.WORKER);

        return WorkerDetailResponse.toDTO(worker, user);
    }

    public WorkerDetailResponse getDetailById(Long workerId) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("工人不存在"));
        User user = userService.getById(workerId);
        return WorkerDetailResponse.toDTO(worker, user);
    }

    public WorkerDetailResponse updateProfile(Long userId, @Valid UpdateWorkerProfileRequest body) {
        Worker worker = workerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("工人不存在"));

        if (body.getRealName() != null) {
            worker.setRealName(body.getRealName());
        }
        if (body.getCity() != null) {
            worker.setCity(body.getCity());
        }
        if (body.getIsPlatformWorker() != null) {
            worker.setIsPlatformWorker(body.getIsPlatformWorker());
        }
        if (body.getWorkStatus() != null) {
            worker.setWorkStatus(body.getWorkStatus());
        }
        if (body.getEnabled() != null) {
            worker.setEnabled(body.getEnabled());
        }

        workerRepository.save(worker);

        User user = userService.getById(userId);
        return WorkerDetailResponse.toDTO(worker, user);
    }

    public List<WorkerDetailResponse> list(String city, Worker.WorkStatus workStatus, Boolean enabled) {
        List<Worker> workers;
        if (city != null && workStatus != null && enabled != null) {
            workers = workerRepository.findByCityAndWorkStatusAndEnabled(city, workStatus, enabled);
        } else if (city != null) {
            workers = workerRepository.findByCity(city);
        } else {
            workers = workerRepository.findAll();
        }

        return workers.stream()
                .map(worker -> WorkerDetailResponse.toDTO(worker, userService.getById(worker.getUserId())))
                .toList();
    }

    public List<Worker> findAvailableWorkers(
            WorkerSkill.WorkerType mainWorkerType,
            Integer requiredCount,
            String city,
            LocalDateTime expectedStartAt,
            LocalDateTime expectedEndAt) {

        // 1️⃣ 查询符合城市、平台工人且空闲的工人
        List<Worker> platformWorkers = workerRepository
                .findByCityAndIsPlatformWorkerAndWorkStatus(city, true, Worker.WorkStatus.IDLE);

        // 2️⃣ 根据 worker_skill 过滤符合工种的工人
        List<Long> workerIdsWithSkill = workerSkillRepository
                .findByWorkerType(mainWorkerType)
                .stream()
                .map(WorkerSkill::getWorkerId)
                .toList();

        List<Worker> candidates = platformWorkers.stream()
                .filter(w -> workerIdsWithSkill.contains(w.getUserId()))
                .sorted(Comparator.comparing(Worker::getRating).reversed()) // 按 rating 排序
                .toList();

        // 3️⃣ 过滤掉时间冲突的工人
        List<Worker> availableWorkers = candidates.stream()
                .filter(worker -> {
                    List<StageAssignment> assignments = stageAssignmentRepository
                            .findByWorkerIdAndStatusIn(
                                    worker.getUserId(),
                                    List.of(StageAssignment.AssignmentStatus.PENDING, StageAssignment.AssignmentStatus.IN_PROGRESS)
                            );
                    // 如果 assignments 中没有与目标时间段冲突的记录，则可用
                    return assignments.stream().noneMatch(a ->
                            a.getExpectedStartAt().isBefore(expectedEndAt) &&
                                    a.getExpectedEndAt().isAfter(expectedStartAt)
                    );
                })
                .limit(requiredCount) // 取前 requiredCount 个
                .toList();

        if (availableWorkers.size() < requiredCount) {
            throw new RuntimeException("可用工人不足");
        }

        return availableWorkers;
    }

    public Worker findReplacementWorker(
            WorkerSkill.WorkerType mainWorkerType,
            String city,
            LocalDateTime expectedStartAt,
            LocalDateTime expectedEndAt,
            Set<Long> excludedWorkerIds) {

        List<Worker> platformWorkers = workerRepository
                .findByCityAndIsPlatformWorkerAndWorkStatus(city, true, Worker.WorkStatus.IDLE);

        List<Long> workerIdsWithSkill = workerSkillRepository
                .findByWorkerType(mainWorkerType)
                .stream()
                .map(WorkerSkill::getWorkerId)
                .toList();

        List<Worker> candidates = platformWorkers.stream()
                .filter(w -> workerIdsWithSkill.contains(w.getUserId()))
                .filter(w -> excludedWorkerIds == null || !excludedWorkerIds.contains(w.getUserId()))
                .sorted(Comparator.comparing(Worker::getRating).reversed())
                .toList();

        return candidates.stream()
                .filter(worker -> {
                    List<StageAssignment> assignments = stageAssignmentRepository
                            .findByWorkerIdAndStatusIn(
                                    worker.getUserId(),
                                    List.of(StageAssignment.AssignmentStatus.PENDING, StageAssignment.AssignmentStatus.IN_PROGRESS)
                            );
                    return assignments.stream().noneMatch(a ->
                            a.getExpectedStartAt().isBefore(expectedEndAt) &&
                                    a.getExpectedEndAt().isAfter(expectedStartAt)
                    );
                })
                .findFirst()
                .orElseThrow(() -> new RuntimeException("可用替补工人不足"));
    }



}
