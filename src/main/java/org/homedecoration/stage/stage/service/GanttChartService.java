package org.homedecoration.stage.stage.service;

import lombok.RequiredArgsConstructor;
import org.homedecoration.stage.assignment.entity.StageAssignment;
import org.homedecoration.stage.assignment.repository.StageAssignmentRepository;
import org.homedecoration.stage.assignment.service.StageAssignmentService;
import org.homedecoration.stage.stage.dto.response.HouseStageMaterialsResponse;
import org.homedecoration.stage.stage.dto.response.HouseStageResponse;
import org.homedecoration.stage.stage.entity.Stage;
import org.homedecoration.stage.stage.repository.StageRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.homedecoration.stage.stage.service.StageService.STATUS_MAP;
import static org.homedecoration.stage.stage.service.StageService.WORKER_TYPE_MAP;

@Service
@RequiredArgsConstructor
public class GanttChartService {

    private final StageRepository stageRepository;
    private final StageAssignmentService stageAssignmentService;

    private static final String CACHE_PREFIX = "stage:";
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取房屋阶段甘特图数据（带 expectedEndAt）
     */
    public HouseStageResponse getGanttData(Long houseId) {
        String cacheKey = CACHE_PREFIX + "materials:" +  ":" + houseId;

        long startTime = System.currentTimeMillis();


        HouseStageResponse cache =
                (HouseStageResponse) redisTemplate.opsForValue().get(cacheKey);

        if (cache != null) {
            System.out.println("[StageMaterials] HIT cache, cost="
                    + (System.currentTimeMillis() - startTime) + "ms");
            return cache;
        }


        List<Stage> stages = stageRepository.findByHouseIdOrderByOrderAsc(houseId);
        HouseStageResponse response = new HouseStageResponse();

        LocalDateTime prevEnd = null;
        for (Stage stage : stages) {
            HouseStageResponse.StageInfo info = new HouseStageResponse.StageInfo();
            info.setOrder(stage.getOrder());
            info.setStageName(stage.getStageName());
            info.setStatus(STATUS_MAP.getOrDefault(stage.getStatus(), stage.getStatus().toString()));
            info.setMainWorkerType(WORKER_TYPE_MAP.getOrDefault(stage.getMainWorkerType(), String.valueOf(stage.getMainWorkerType())));
            info.setRequiredCount(stage.getRequiredCount());
            info.setEstimatedDay(stage.getEstimatedDay());
            info.setStart_at(stage.getStart_at());
            info.setEnd_at(stage.getEnd_at());

            // ✅ 自动填充 expectedStartAt
            LocalDateTime start = stage.getExpectedStartAt();
            if (start == null) {
                if (prevEnd != null) {
                    start = prevEnd.plusDays(1); // 上一阶段结束 + 1 天
                } else {
                    // 如果第一阶段没有设置，从今天 +1 开始
                    start = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0);
                }
            }
            info.setExpectedStartAt(start);

            // ✅ 计算 expectedEndAt = start + estimatedDay - 1
            if (stage.getEstimatedDay() != null) {
                info.setExpectedEndAt(start.plusDays(stage.getEstimatedDay() - 1));
                prevEnd = info.getExpectedEndAt();
            }

            response.getStages().add(info);
        }

        redisTemplate.opsForValue().set(cacheKey, response, 10, TimeUnit.MINUTES);

        System.out.println("[HouseMaterials] MISS cache, cost="
                + (System.currentTimeMillis() - startTime) + "ms");


        return response;
    }


    public Stage getCurrentStage(Long houseId) {
        List<Stage> stages = stageRepository.findByHouseIdOrderByOrderAsc(houseId);

        // 遍历阶段，找到第一个未被验收的阶段
        for (Stage stage : stages) {
            if (stage.getStatus() != Stage.StageStatus.ACCEPTED) {
                return stage; // 直接返回第一个未验收的阶段
            }
        }

        // 如果所有阶段都被验收，则返回最后一个阶段（可选）
        if (!stages.isEmpty()) {
            return stages.get(stages.size() - 1);
        }

        // 如果没有阶段，抛出异常或返回 null（根据业务需求决定）
        throw new RuntimeException("未找到有效的阶段信息");
    }


    public void updateExpectedStartAt(Long houseId, int order, LocalDateTime userInputStart) {
        // 按顺序获取所有阶段
        List<Stage> stages = stageRepository.findByHouseIdOrderByOrderAsc(houseId);

        // ---------- 1️⃣ 校验是否早于最早可开始时间 ----------
        LocalDateTime earliest = null;
        for (Stage stage : stages) {
            if (stage.getOrder() >= order) break;

            if (stage.getExpectedStartAt() != null) {
                earliest = stage.getExpectedStartAt().plusDays(stage.getEstimatedDay());
            } else if (earliest != null) {
                earliest = earliest.plusDays(stage.getEstimatedDay());
            }
        }

        if (earliest != null && userInputStart.isBefore(earliest)) {
            throw new RuntimeException("预计开始时间不能早于最早可开始时间 " + earliest);
        }

        // ---------- 2️⃣ 更新目标阶段 ----------
        Stage target = stages.stream()
                .filter(s -> s.getOrder() == order)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("阶段不存在"));

        target.setExpectedStartAt(userInputStart);

        // ---------- 3️⃣ 向后调整冲突阶段 ----------
        LocalDateTime prevEnd = userInputStart.plusDays(target.getEstimatedDay());
        for (Stage nextStage : stages) {
            if (nextStage.getOrder() <= order) continue; // 跳过之前阶段

            LocalDateTime nextStart = nextStage.getExpectedStartAt();
            if (nextStart == null || !nextStart.isAfter(prevEnd)) {
                // 如果后续阶段为空或开始时间早于前一阶段结束，则推后
                nextStage.setExpectedStartAt(prevEnd);
            }
            // 更新 prevEnd
            prevEnd = nextStage.getExpectedStartAt().plusDays(nextStage.getEstimatedDay());
        }

        Stage current = getCurrentStage(houseId);
        boolean isCurrentStage = current.getOrder().equals(order);

        if (isCurrentStage) {
            // 只触发通知/调用 assignmentService 去更新预约时间
            stageAssignmentService.updateAssignmentsForStage(current.getId(), userInputStart);
        }

        // ---------- 4️⃣ 批量保存 ----------
        stageRepository.saveAll(stages);
    }




}
