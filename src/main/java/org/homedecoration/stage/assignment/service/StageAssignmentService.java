package org.homedecoration.stage.assignment.service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.homedecoration.house.entity.House;
import org.homedecoration.house.service.HouseService;
import org.homedecoration.identity.worker.LeaveRecord.LeaveRecord;
import org.homedecoration.identity.worker.LeaveRecord.LeaveRecordRepository;
import org.homedecoration.identity.worker.dto.request.LeaveRequest;
import org.homedecoration.identity.worker.dto.response.WorkerSimpleResponse;
import org.homedecoration.identity.worker.entity.Worker;
import org.homedecoration.identity.worker.service.WorkerService;
import org.homedecoration.identity.worker.dto.response.WorkerStageCalendarResponse;
import org.homedecoration.layout.entity.HouseLayout;
import org.homedecoration.layout.repository.HouseLayoutRepository;
import org.homedecoration.layoutImage.entity.HouseLayoutImage;
import org.homedecoration.layoutImage.repository.HouseLayoutImageRepository;
import org.homedecoration.stage.assignment.dto.request.CreateStageAssignmentRequest;
import org.homedecoration.stage.assignment.dto.request.UpdateStageAssignmentRequest;
import org.homedecoration.stage.assignment.entity.StageAssignment;
import org.homedecoration.stage.assignment.repository.StageAssignmentRepository;
import org.homedecoration.stage.stage.dto.response.HouseStageMaterialsResponse;
import org.homedecoration.stage.stage.entity.Stage;
import org.homedecoration.stage.stage.service.StageService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageAssignmentService {
    private final StageAssignmentRepository stageAssignmentRepository;
    private final StageService stageService;
    private final WorkerService workerService;
    private final HouseService houseService;
    private final LeaveRecordRepository leaveRecordRepository;
    private final HouseLayoutRepository houseLayoutRepository;
    private final HouseLayoutImageRepository houseLayoutImageRepository;

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

    // 文件路径: D:\CODE\home_decoration_backend\src\main\java\org\homedecoration\stage\assignment\service\StageAssignmentService.java

    public WorkerStageCalendarResponse getWorkerStageCalendar(Long workerId, YearMonth yearMonth) {
        LocalDateTime monthStart = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        // 获取该工人在指定月份的任务分配
        List<StageAssignment> assignments = stageAssignmentRepository
                .findByWorkerIdAndStatusInAndExpectedStartAtBeforeAndExpectedEndAtAfter(
                        workerId,
                        List.of(StageAssignment.AssignmentStatus.PENDING,
                                StageAssignment.AssignmentStatus.IN_PROGRESS,
                                StageAssignment.AssignmentStatus.COMPLETED),
                        monthEnd,
                        monthStart
                );

        // 获取该工人在指定月份的请假记录
        List<LeaveRecord> leaveRecords = leaveRecordRepository.findByWorkerId(workerId)
                .stream()
                .filter(record -> !record.getLeaveDate().isBefore(yearMonth.atDay(1)) &&
                        !record.getLeaveDate().isAfter(yearMonth.atEndOfMonth()))
                .toList();

        // 提取请假日期
        List<LocalDate> leaveDays = leaveRecords.stream()
                .map(LeaveRecord::getLeaveDate)
                .toList();

        // 构建响应对象
        WorkerStageCalendarResponse response = new WorkerStageCalendarResponse();
        List<WorkerStageCalendarResponse.StageAssignmentItem> items = assignments.stream()
                .map(assignment -> toWorkerStageAssignmentItem(workerId, assignment))
                .toList();

        response.setAssignments(items);
        response.setLeaveDays(leaveDays); // 设置请假日期

        return response;
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

    private WorkerStageCalendarResponse.StageAssignmentItem toWorkerStageAssignmentItem(
            Long workerId,
            StageAssignment assignment) {
        WorkerStageCalendarResponse.StageAssignmentItem item = new WorkerStageCalendarResponse.StageAssignmentItem();
        item.setAssignmentId(assignment.getId());
        if (assignment.getExpectedStartAt() != null) {
            item.setExpected_Start_at(assignment.getExpectedStartAt().toLocalDate());
        }
        if (assignment.getExpectedEndAt() != null) {
            item.setExpected_End_at(assignment.getExpectedEndAt().toLocalDate().minusDays(1));
        }

        item.setStatus(assignment.getStatus());

        Stage stage = stageService.getStage(assignment.getStageId());
        item.setStageId(stage.getId());
        item.setStageName(stage.getStageName());

        House house = houseService.getHouseById(stage.getHouseId());
        item.setHouseId(house.getId());
        item.setCity(house.getCity());
        item.setCommunityName(house.getCommunityName());
        item.setBuildingNo(house.getBuildingNo());
        item.setUnitNo(house.getUnitNo());
        item.setRoomNo(house.getRoomNo());
        item.setArea(house.getArea());

        HouseLayout layout = (HouseLayout) houseLayoutRepository.findByHouseIdAndLayoutStatus(house.getId(), HouseLayout.LayoutStatus.CONFIRMED)
                .orElseThrow(() -> new RuntimeException("无确认布局"));
        HouseLayoutImage designingImageUrl = houseLayoutImageRepository.findByLayoutIdAndImageType(layout.getId(), HouseLayoutImage.ImageType.FINAL);
        item.setDesignation_image_url(designingImageUrl.getImageUrl());

        List<WorkerSimpleResponse> coworkers = stageAssignmentRepository.findByStageId(stage.getId())
                .stream()
                .filter(stageAssignment -> stageAssignment.getStatus() != StageAssignment.AssignmentStatus.CANCELLED)
                .filter(stageAssignment -> !stageAssignment.getWorkerId().equals(workerId)) // 排除当前工人
                .map(stageAssignment -> {
                    WorkerSimpleResponse coworker = workerService.getSimpleResponse(stageAssignment.getWorkerId());
                    // 设置新增字段
                    coworker.setExpected_Start_at(stageAssignment.getExpectedStartAt().toLocalDate());
                    coworker.setExpected_End_at(stageAssignment.getExpectedEndAt().toLocalDate().minusDays(1)); // 减去一天以匹配前端需求
                    return coworker;
                })
                .distinct()
                .toList();


        item.setCoworkers(coworkers);

        HouseStageMaterialsResponse materials = stageService.getHouseMaterialsByStage(
                house.getId(),
                house.getUser().getId()
        );
        materials.getStages().stream()
                .filter(stageMaterial -> stageMaterial.getStage().equals(stage.getOrder()))
                .findFirst()
                .ifPresent(stageMaterial -> {
                    item.setMainMaterials(stageMaterial.getMainMaterials().stream().collect(Collectors.toList()));
                    item.setAuxiliaryMaterials(stageMaterial.getAuxiliaryMaterials().stream().collect(Collectors.toList()));
                });

        return item;
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


    @Transactional
    public void applyLeaveForDate(Long workerId, LeaveRequest request) {
        LocalDate leaveDate = request.getLeaveDate();

        LocalDateTime leaveStart = leaveDate.atStartOfDay();
        LocalDateTime leaveEnd = leaveDate.atTime(LocalTime.MAX); // 当天结束

        // 1️⃣ 查找当天命中的 assignment（最多一个）
        List<StageAssignment> assignments =
                stageAssignmentRepository.findByWorkerIdAndStatusInAndExpectedStartAtBeforeAndExpectedEndAtAfter(
                        workerId,
                        List.of(StageAssignment.AssignmentStatus.PENDING, StageAssignment.AssignmentStatus.IN_PROGRESS),
                        leaveEnd,
                        leaveStart
                );

        // 没有 assignment → 直接创建请假占位
        if (assignments.isEmpty()) {
            createLeaveAssignment(workerId, request);
            return ;
        }

        StageAssignment assignment = assignments.get(0);
        Stage stage = stageService.getStage(assignment.getStageId());
        House house = houseService.getHouseById(stage.getHouseId());

        LocalDate assignmentStart = assignment.getExpectedStartAt().toLocalDate();
        LocalDate assignmentEnd = assignment.getExpectedEndAt().toLocalDate();


        // ========= 情况一：请假在 assignment 开始前，整体换人 =========
        if (assignment.getStatus().equals(StageAssignment.AssignmentStatus.PENDING)) {

            Worker replacement = findReplacement(stage, house, leaveStart, leaveEnd);
            assignment.setWorkerId(replacement.getUserId());
            stageAssignmentRepository.save(assignment);
            createLeaveAssignment(workerId, request);
            return ;
        }

        // ========= 情况二：请假在 assignment 期间，拆分 =========
        if(assignment.getStatus().equals(StageAssignment.AssignmentStatus.IN_PROGRESS)){

            // ② 创建请假占位
            createLeaveAssignment(workerId, request);

            // ③ 剩余天数找替补
            if (leaveDate.isBefore(assignmentEnd)) {

                LocalDateTime replacementStart = leaveDate.atStartOfDay();
                LocalDateTime replacementEnd = assignment.getExpectedEndAt(); // 原 assignment 结束时间
                assignment.setExpectedEndAt(leaveDate.atStartOfDay());
                stageAssignmentRepository.save(assignment);

                Worker replacement = findReplacement(stage, house, replacementStart, replacementEnd);

                StageAssignment newAssignment = new StageAssignment();
                newAssignment.setStageId(stage.getId());
                newAssignment.setWorkerId(replacement.getUserId());
                newAssignment.setExpectedStartAt(replacementStart);
                newAssignment.setStartAt(replacementStart);
                newAssignment.setExpectedEndAt(replacementEnd);
                newAssignment.setStatus(StageAssignment.AssignmentStatus.IN_PROGRESS);

                stageAssignmentRepository.save(newAssignment);
            }
        }

    }

    /**
     * 创建请假占位
     */
    private void createLeaveAssignment(Long workerId, LeaveRequest request) {

        LeaveRecord record = new LeaveRecord();
        record.setWorkerId(workerId);
        record.setLeaveDate(request.getLeaveDate());
        record.setLeaveType(request.getLeaveType());
        record.setReason(request.getReason());
        leaveRecordRepository.save(record);
    }

    /**
     * 找替补工人，严格按时间段找
     */
    private Worker findReplacement(Stage stage, House house, LocalDateTime expectedStartAt, LocalDateTime expectedEndAt) {
        List<Worker> workers = workerService.findAvailableWorkers(
                stage.getMainWorkerType(),
                1,
                house.getCity(),
                expectedStartAt,
                expectedEndAt
        );

        if (workers.isEmpty()) {
            throw new RuntimeException("找不到可替换工人");
        }

        return workers.get(0);
    }

    @Transactional
    public void cancelLeaveForDate(Long userId, @NotNull LocalDate leaveDate) {
        leaveRecordRepository.deleteByWorkerIdAndLeaveDate(userId, leaveDate);
    }
}
