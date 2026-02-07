package org.homedecoration.identity.worker.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.identity.user.repository.UserRepository;
import org.homedecoration.identity.user.service.UserService;
import org.homedecoration.identity.worker.LeaveRecord.LeaveRecord;
import org.homedecoration.identity.worker.LeaveRecord.LeaveRecordRepository;
import org.homedecoration.identity.worker.dto.request.CreateWorkerRequest;
import org.homedecoration.identity.worker.dto.request.UpdateWorkerProfileRequest;
import org.homedecoration.identity.worker.dto.response.LaborMarketResponse;
import org.homedecoration.identity.worker.dto.response.WorkerDetailResponse;
import org.homedecoration.identity.worker.dto.response.WorkerResponse;
import org.homedecoration.identity.worker.dto.response.WorkerSimpleResponse;
import org.homedecoration.identity.worker.entity.Worker;
import org.homedecoration.identity.worker.repository.WorkerRepository;
import org.homedecoration.identity.worker.worker_skill.entity.WorkerSkill;
import org.homedecoration.identity.worker.worker_skill.repository.WorkerSkillRepository;
import org.homedecoration.stage.stage.entity.Stage;
import org.homedecoration.stage.stage.service.StageService;
import org.homedecoration.stage.assignment.entity.StageAssignment;
import org.homedecoration.stage.assignment.repository.StageAssignmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final UserService userService;
    private final StageAssignmentRepository stageAssignmentRepository;
    private final WorkerSkillRepository workerSkillRepository;
    private final LeaveRecordRepository leaveRecordRepository;
    private final StageService stageService;

    public WorkerDetailResponse apply(Long userId, @Valid CreateWorkerRequest request) {
        // 先校验用户是否存在
        User user = userService.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 再校验是否已经是工人
        if (workerRepository.existsById(userId)) {
            throw new IllegalStateException("用户已是工人");
        }

        // 创建 Worker 实体
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

        // 更新用户角色
        userService.updateRole(userId, User.Role.WORKER);

        // 设置关联关系并保存
        worker.setUser(user);
        workerRepository.save(worker);

        return WorkerDetailResponse.toDTO(worker, user);
    }


    public WorkerDetailResponse getDetailById(Long workerId) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("工人不存在"));
        User user = userService.getById(workerId);
        return WorkerDetailResponse.toDTO(worker, user);
    }

    public WorkerSimpleResponse getSimpleResponse(Long workerId) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("工人不存在"));
        User user = userService.getById(workerId);
        WorkerSimpleResponse response = new WorkerSimpleResponse();
        response.setUserId(worker.getUserId());
        if (user != null) {
            response.setUsername(user.getUsername());
            response.setAvatarUrl(user.getAvatarUrl());
            response.setPhone(user.getPhone());
            response.setEmail(user.getEmail());
        }
        response.setRealName(worker.getRealName());
        return response;
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

    public Page<WorkerResponse> findAvailableWorkersForSelection(
            WorkerSkill.WorkerType mainWorkerType,
            WorkerSkill.Level minLevel,
            String city,
            LocalDateTime expectedStartAt,
            LocalDateTime expectedEndAt,
            Pageable pageable
    ) {
        // 1️⃣ 获取符合条件的候选工人
        List<Worker> candidates = findAvailableWorkerCandidates(
                mainWorkerType, minLevel, city, expectedStartAt, expectedEndAt
        );

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), candidates.size());

        if (start >= candidates.size()) {
            return Page.empty(pageable);
        }

        // 2️⃣ 分页切片
        List<Worker> pageList = candidates.subList(start, end);

        // 3️⃣ 映射成 WorkerResponse
        List<WorkerResponse> responseList = pageList.stream()
                .map(worker -> {
                    WorkerResponse resp = new WorkerResponse();
                    resp.setUserId(worker.getUserId());

                    // 用户信息
                    User user = userService.getById(worker.getUserId());
                    resp.setUsername(user.getUsername());
                    resp.setAvatarUrl(user.getAvatarUrl());
                    resp.setPhone(user.getPhone());
                    resp.setEmail(user.getEmail());

                    resp.setRealName(worker.getRealName());
                    resp.setRating(worker.getRating());

                    // 取该工人对应工种的最高等级
                    workerSkillRepository.findByWorkerIdAndWorkerType(worker.getUserId(), mainWorkerType)
                            .stream()
                            .max(Comparator.comparingInt(ws -> ws.getLevel().ordinal()))
                            .ifPresent(ws -> resp.setLevel(ws.getLevel()));

                    return resp;
                })
                .toList();

        return new PageImpl<>(responseList, pageable, candidates.size());
    }

    public LaborMarketResponse getLaborMarketResponse(Long stageId, WorkerSkill.Level minLevel, Pageable pageable) {
        Stage stage = stageService.getStage(stageId);
        if (stage.getExpectedStartAt() == null) {
            throw new IllegalStateException("阶段未设置预计开始时间");
        }
        if (stage.getEstimatedDay() == null) {
            throw new IllegalStateException("阶段未设置预计工期");
        }

        String city = stage.getHouse().getCity();
        LocalDateTime expectedStartAt = stage.getExpectedStartAt();
        LocalDateTime expectedEndAt = expectedStartAt.plusDays(stage.getEstimatedDay());

        Page<WorkerResponse> availableWorkers = findAvailableWorkersForSelection(
                stage.getMainWorkerType(),
                minLevel,
                city,
                expectedStartAt,
                expectedEndAt,
                pageable
        );

        LaborMarketResponse response = new LaborMarketResponse();
        response.setStageId(stage.getId());
        response.setMainWorkerType(stage.getMainWorkerType().name());
        response.setRequiredCount(stage.getRequiredCount());
        response.setExpectedStartAt(expectedStartAt.toString());
        response.setEstimatedDay(stage.getEstimatedDay());
        response.setWorkers(availableWorkers.getContent());
        return response;
    }


    public List<Worker> findAvailableWorkerCandidates(
            WorkerSkill.WorkerType mainWorkerType,
            WorkerSkill.Level minLevel, // ✅ 新增熟练度参数
            String city,
            LocalDateTime expectedStartAt,
            LocalDateTime expectedEndAt
    ) {
        // 1️⃣ 查询符合城市、非平台工人且空闲的工人
        List<Worker> workers = workerRepository
                .findByCityAndIsPlatformWorkerAndWorkStatus(
                        city, false, Worker.WorkStatus.IDLE
                );

        // 2️⃣ 获取符合工种并且等级 ≥ minLevel 的工人
        Set<Long> qualifiedWorkerIds = workerSkillRepository
                .findByWorkerType(mainWorkerType)
                .stream()
                .filter(ws -> ws.getLevel().ordinal() >= minLevel.ordinal()) // ✅ ordinal 比较
                .collect(Collectors.groupingBy(WorkerSkill::getWorkerId,
                        Collectors.maxBy(Comparator.comparingInt(ws -> ws.getLevel().ordinal()))))
                .keySet();

        return workers.stream()
                .filter(w -> qualifiedWorkerIds.contains(w.getUserId()))
                .sorted(Comparator.comparing(Worker::getRating).reversed())
                .filter(worker -> {
                    // 工作冲突
                    boolean hasWorkConflict = stageAssignmentRepository
                            .findByWorkerIdAndStatusIn(
                                    worker.getUserId(),
                                    List.of(
                                            StageAssignment.AssignmentStatus.PENDING,
                                            StageAssignment.AssignmentStatus.IN_PROGRESS
                                    )
                            )
                            .stream()
                            .filter(a -> a.getType() == StageAssignment.AssignmentType.WORK)
                            .anyMatch(a ->
                                    a.getExpectedStartAt().isBefore(expectedEndAt) &&
                                            a.getExpectedEndAt().isAfter(expectedStartAt)
                            );

                    if (hasWorkConflict) return false;

                    // 请假冲突
                    boolean hasLeaveConflict = leaveRecordRepository
                            .findByWorkerId(worker.getUserId())
                            .stream()
                            .anyMatch(l ->
                                    !l.getLeaveDate().isBefore(expectedStartAt.toLocalDate()) &&
                                            !l.getLeaveDate().isAfter(expectedEndAt.toLocalDate())
                            );

                    return !hasLeaveConflict;
                })
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

        List<Worker> availableWorkers = candidates.stream()
                .filter(worker -> {
                    // 1️⃣ 检查工作任务冲突（WORK 类型 assignment）
                    List<StageAssignment> assignments = stageAssignmentRepository
                            .findByWorkerIdAndStatusIn(
                                    worker.getUserId(),
                                    List.of(StageAssignment.AssignmentStatus.PENDING, StageAssignment.AssignmentStatus.IN_PROGRESS)
                            );

                    List<StageAssignment> workAssignments = assignments.stream()
                            .filter(a -> a.getType() == StageAssignment.AssignmentType.WORK)
                            .toList();

                    boolean hasWorkConflict = workAssignments.stream().anyMatch(a ->
                            a.getExpectedStartAt().isBefore(expectedEndAt) &&
                                    a.getExpectedEndAt().isAfter(expectedStartAt)
                    );

                    if (hasWorkConflict) return false;

                    // 2️⃣ 检查请假冲突（LeaveRecord）
                    List<LeaveRecord> leaves = leaveRecordRepository
                            .findByWorkerId(worker.getUserId());

                    boolean hasLeaveConflict = leaves.stream().anyMatch(l ->
                            !l.getLeaveDate().isBefore(expectedStartAt.toLocalDate()) &&
                                    !l.getLeaveDate().isAfter(expectedEndAt.toLocalDate())
                    );

                    return !hasLeaveConflict;
                })
                .limit(requiredCount)
                .toList();



        if (availableWorkers.size() < requiredCount) {
            throw new RuntimeException("可用工人不足");
        }

        return availableWorkers;
    }


}
