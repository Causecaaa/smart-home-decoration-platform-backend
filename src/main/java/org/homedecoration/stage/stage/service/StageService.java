package org.homedecoration.stage.stage.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.identity.user.service.UserService;
import org.homedecoration.identity.worker.entity.Worker;
import org.homedecoration.identity.worker.service.WorkerService;
import org.homedecoration.identity.worker.worker_skill.repository.WorkerSkillRepository;
import org.homedecoration.layout.entity.HouseLayout;
import org.homedecoration.layout.repository.HouseLayoutRepository;
import org.homedecoration.layoutImage.entity.HouseLayoutImage;
import org.homedecoration.layoutImage.repository.HouseLayoutImageRepository;
import org.homedecoration.stage.assignment.entity.StageAssignment;
import org.homedecoration.stage.assignment.repository.StageAssignmentRepository;
import org.homedecoration.stage.stage.dto.response.HouseStageMaterialsResponse;
import org.homedecoration.stage.stage.dto.response.HouseStageResponse;
import org.homedecoration.stage.stage.dto.response.StageDetailResponse;
import org.homedecoration.stage.stage.entity.Stage;
import org.homedecoration.stage.stage.repository.StageRepository;
import org.homedecoration.furniture.SchemeRoomMaterial.entity.SchemeRoomMaterial;
import org.homedecoration.furniture.SchemeRoomMaterial.service.SchemeRoomMaterialService;
import org.homedecoration.house.dto.response.HouseMaterialSummaryResponse;
import org.homedecoration.house.entity.House;
import org.homedecoration.house.service.HouseService;
import org.homedecoration.houseRoom.entity.HouseRoom;
import org.homedecoration.houseRoom.service.HouseRoomService;
import org.homedecoration.identity.worker.worker_skill.entity.WorkerSkill;
import org.homedecoration.material.auxiliary.AuxiliaryMaterial;
import org.homedecoration.material.auxiliary.AuxiliaryMaterialRepository;
import org.homedecoration.material.material.Material;
import org.homedecoration.material.material.MaterialRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageService {

    // 阶段映射常量
    private static final Map<Integer, String> STAGE_MAP = Map.of(
            1, "拆改阶段",
            2, "水电改造",
            3, "泥工阶段",
            4, "木工阶段",
            5, "油漆 / 面漆阶段",
            6, "瓦工 / 瓷砖铺贴",
            7, "收尾 / 清洁阶段"
    );

    // 依赖注入
    private final HouseService houseService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final StageRepository stageRepository;
    private final HouseRoomService houseRoomService;
    private final MaterialRepository materialRepository;
    private final AuxiliaryMaterialRepository auxiliaryMaterialRepository;
    private final SchemeRoomMaterialService schemeRoomMaterialService;
    private final WorkerSkillRepository workerSkillRepository;
    private final StageAssignmentRepository stageAssignmentRepository;
    private final HouseLayoutRepository houseLayoutRepository;
    private final HouseLayoutImageRepository houseLayoutImageRepository;


    public Stage getStage(Long stageId) {
        return stageRepository.findById(stageId)
                .orElseThrow(() -> new RuntimeException("阶段不存在"));
    }


    /**
     * 获取按阶段分配的房屋材料清单
     */
    public HouseStageMaterialsResponse getHouseMaterialsByStage(Long houseId, Long userId) {
        if(!houseService.getHouseById(houseId).getUser().getId().equals(userId)){
            throw new RuntimeException("无权限访问");
        }
        long start = System.currentTimeMillis();

        String cacheKey = "stage:materials:" + userId + ":" + houseId;

        HouseStageMaterialsResponse cache =
                (HouseStageMaterialsResponse) redisTemplate.opsForValue().get(cacheKey);

        if (cache != null) {
            System.out.println("[StageMaterials] HIT cache, cost="
                    + (System.currentTimeMillis() - start) + "ms");
            return cache;
        }

        HouseMaterialSummaryResponse allMaterials = calculateHouseMaterials(houseId);

        HouseStageMaterialsResponse response = new HouseStageMaterialsResponse();

        // 记录主材每个 type+displayName 的总面积，以及派送阶段
        Map<String, HouseStageMaterialsResponse.MaterialInfo> mainMaterialMap = new LinkedHashMap<>();
        Map<String, Integer> mainMaterialStageMap = new HashMap<>();

        // 1️⃣ 处理主材
        for (Map.Entry<String, List<HouseMaterialSummaryResponse.MaterialDetail>> entry : allMaterials.getMainMaterials().entrySet()) {
            String type = entry.getKey();
            for (HouseMaterialSummaryResponse.MaterialDetail m : entry.getValue()) {
                String key = type + "-" + m.getDisplayName();

                // 如果已经记录，累加面积
                mainMaterialMap.compute(key, (k, v) -> {
                    if (v == null) {
                        HouseStageMaterialsResponse.MaterialInfo info = new HouseStageMaterialsResponse.MaterialInfo();
                        info.setType(type);
                        info.setDisplayName(m.getDisplayName());
                        info.setArea(m.getArea());
                        return info;
                    } else {
                        v.setArea(v.getArea().add(m.getArea()));
                        return v;
                    }
                });

                // 记录第一次出现的阶段
                if (!mainMaterialStageMap.containsKey(key)) {
                    int stage = getStageForMaterial(type);
                    mainMaterialStageMap.put(key, stage);
                }
            }
        }

        // 2️⃣ 处理辅材（第一次出现的阶段派送一次）
        Map<String, HouseStageMaterialsResponse.AuxMaterialInfo> auxMaterialMap = new LinkedHashMap<>();
        Map<String, Integer> auxMaterialStageMap = new HashMap<>();
        for (HouseMaterialSummaryResponse.AuxiliaryMaterialItem aux : allMaterials.getAuxiliaryMaterials()) {
            String key = aux.getName();

            // 累加数量
            auxMaterialMap.compute(key, (k, v) -> {
                if (v == null) {
                    HouseStageMaterialsResponse.AuxMaterialInfo info = new HouseStageMaterialsResponse.AuxMaterialInfo();
                    info.setName(aux.getName());
                    info.setCategory(aux.getCategory());
                    info.setUnit(aux.getUnit());
                    info.setQuantity(aux.getQuantity());
                    return info;
                } else {
                    v.setQuantity(v.getQuantity().add(aux.getQuantity()));
                    return v;
                }
            });

            // 记录第一次使用的阶段
            if (!auxMaterialStageMap.containsKey(key)) {
                int stage = getStageForAuxiliary(aux.getCategory());
                auxMaterialStageMap.put(key, stage);
            }
        }

        // 3️⃣ 构建阶段列表 - 按序号排序
        List<Map.Entry<Integer, String>> sortedStages = STAGE_MAP.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toList());

        for (Map.Entry<Integer, String> stageEntry : sortedStages) {
            Integer stageNum = stageEntry.getKey();
            String stageName = stageEntry.getValue();

            HouseStageMaterialsResponse.StageMaterial stageMaterial = new HouseStageMaterialsResponse.StageMaterial();
            stageMaterial.setStage(stageNum);
            stageMaterial.setStageName(stageName);

            // 添加主材
            mainMaterialMap.forEach((k, v) -> {
                if (mainMaterialStageMap.get(k).equals(stageNum)) {
                    stageMaterial.getMainMaterials().add(v);
                }
            });

            // 添加辅材
            auxMaterialMap.forEach((k, v) -> {
                if (auxMaterialStageMap.get(k).equals(stageNum)) {
                    stageMaterial.getAuxiliaryMaterials().add(v);
                }
            });

            response.getStages().add(stageMaterial);
        }

        redisTemplate.opsForValue().set(
                cacheKey,
                response,
                10,
                TimeUnit.MINUTES
        );

        System.out.println("[StageMaterials] MISS cache, cost="
                + (System.currentTimeMillis() - start) + "ms");

        return response;
    }

    /**
     * 主材派送阶段规则
     */
    private int getStageForMaterial(String type) {
        return switch (type) {
            case "FLOOR" -> 6;                 // 地面材料在瓦工阶段铺设
            case "CABINET" -> 4;               // 木工阶段做柜子
            case "WALL", "CEILING" -> 5;       // 油漆阶段
            default -> 6;                      // 瓦工/其他
        };
    }

    /**
     * 辅材派送阶段规则
     */
    private int getStageForAuxiliary(String category) {
        return switch (category) {
            case "PIPE", "WIRE" -> 2;
            case "CEMENT", "FIXING_MATERIALS" -> 3;
            case "ETC" -> 4; // ETC 小件提前到第一次使用阶段
            default -> 6;
        };
    }

    /**
     * 异步创建装修阶段
     */
    @Async
    public void createStagesAsync(Long houseId) {
        System.out.println("[StageService] 异步方法 createStagesAsync 开始，houseId=" + houseId);
        createStagesForHouse(houseId); // 复用已有方法
        System.out.println("[StageService] 异步方法 createStagesAsync 结束，houseId=" + houseId);
    }

    /**
     * 为房屋创建装修阶段
     */
    public void createStagesForHouse(Long houseId) {
        System.out.println("[StageService] createStagesForHouse 开始，houseId=" + houseId);

        House house = houseService.getHouseById(houseId);
        BigDecimal houseArea = house.getArea(); // 房屋总面积
        int roomCount = houseRoomService.getConfirmedRoomsByHouseId(houseId).size();
        System.out.println("[StageService] 房屋总面积: " + houseArea + ", 房间数量: " + roomCount);

        List<Stage> stages = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : STAGE_MAP.entrySet()) {
            Stage stage = new Stage();
            stage.setHouseId(houseId);
            stage.setOrder(entry.getKey());
            stage.setStageName(entry.getValue());
            stage.setStatus(Stage.StageStatus.PENDING);
            stage.setMainWorkerType(mapStageToWorkerType(entry.getValue()));

            // 计算人数和预计天数
            int requiredCount = calculateRequiredCount(entry.getValue(), houseArea, roomCount);
            int estimatedDay = calculateEstimatedDays(entry.getValue(), houseArea, roomCount);
            stage.setRequiredCount(requiredCount);
            stage.setEstimatedDay(estimatedDay);

            System.out.println("[StageService] 创建阶段: " + entry.getValue() +
                    ", 所需人数: " + requiredCount +
                    ", 预计天数: " + estimatedDay);

            stages.add(stage);
        }

        stageRepository.saveAll(stages);
        System.out.println("[StageService] 所有阶段已保存，houseId=" + houseId + ", 阶段数量=" + stages.size());
    }

    /**
     * 计算所需工人数量
     */
    private int calculateRequiredCount(String stageName, BigDecimal area, int roomCount) {
        // 基础人数根据面积和房间数
        int baseCount = switch (stageName) {
            case "拆改阶段" -> area.divide(new BigDecimal("30"), 0, BigDecimal.ROUND_UP).intValue();
            case "水电改造" -> area.divide(new BigDecimal("50"), 0, BigDecimal.ROUND_UP).intValue();
            case "泥工阶段" -> area.divide(new BigDecimal("40"), 0, BigDecimal.ROUND_UP).intValue();
            case "木工阶段" -> area.divide(new BigDecimal("30"), 0, BigDecimal.ROUND_UP).intValue();
            case "油漆 / 面漆阶段" -> area.divide(new BigDecimal("50"), 0, BigDecimal.ROUND_UP).intValue();
            case "瓦工 / 瓷砖铺贴" -> area.divide(new BigDecimal("40"), 0, BigDecimal.ROUND_UP).intValue();
            case "安装阶段" -> Math.max(1, Math.min(2, roomCount));
            case "收尾 / 清洁阶段" -> area.divide(new BigDecimal("50"), 0, BigDecimal.ROUND_UP).intValue();
            default -> 1;
        };

        // 调整人数：面积大于100平，每阶段增加1人
        if (area.compareTo(new BigDecimal("100")) > 0) {
            baseCount += 1;
        }

        // 最少1人，最多5人限制
        return Math.max(1, Math.min(baseCount, 5));
    }

    /**
     * 计算预计完成天数
     */
    private int calculateEstimatedDays(String stageName, BigDecimal area, int roomCount) {
        int baseDays = switch (stageName) {
            case "拆改阶段" -> 1 + area.divide(new BigDecimal("60"), 0, BigDecimal.ROUND_UP).intValue();
            case "水电改造" -> 1 + area.divide(new BigDecimal("100"), 0, BigDecimal.ROUND_UP).intValue();
            case "泥工阶段" -> 1 + area.divide(new BigDecimal("80"), 0, BigDecimal.ROUND_UP).intValue();
            case "木工阶段" -> 1 + area.divide(new BigDecimal("60"), 0, BigDecimal.ROUND_UP).intValue();
            case "油漆 / 面漆阶段" -> 1 + area.divide(new BigDecimal("150"), 0, BigDecimal.ROUND_UP).intValue();
            case "瓦工 / 瓷砖铺贴" -> 1 + area.divide(new BigDecimal("80"), 0, BigDecimal.ROUND_UP).intValue();
            case "安装阶段" -> Math.max(1, Math.min(2, roomCount));
            case "收尾 / 清洁阶段" -> 1 + area.divide(new BigDecimal("200"), 0, BigDecimal.ROUND_UP).intValue();
            default -> 1;
        };

        // 房间数量大于3，适当增加1天
        if (roomCount > 3) {
            baseDays += 1;
        }

        return baseDays;
    }

    /**
     * 映射阶段到工人类型
     */
    private WorkerSkill.WorkerType mapStageToWorkerType(String stageName) {
        return switch (stageName) {
            case "拆改阶段" -> WorkerSkill.WorkerType.DEMOLITION;
            case "水电改造" -> WorkerSkill.WorkerType.ELECTRICAL;
            case "泥工阶段" -> WorkerSkill.WorkerType.PLASTER;
            case "木工阶段" -> WorkerSkill.WorkerType.CARPENTRY;
            case "油漆 / 面漆阶段" -> WorkerSkill.WorkerType.PAINTING;
            case "瓦工 / 瓷砖铺贴" -> WorkerSkill.WorkerType.TILING;
            default -> WorkerSkill.WorkerType.INSTALLATION;
        };
    }

    public HouseMaterialSummaryResponse calculateHouseMaterials(Long houseId) {
        String cacheKey = "house:materials:summary:" + houseId;
        long start = System.currentTimeMillis();

        HouseMaterialSummaryResponse cache =
                (HouseMaterialSummaryResponse) redisTemplate.opsForValue().get(cacheKey);

        if (cache != null) {
            System.out.println("[HouseMaterials] HIT cache, cost="
                    + (System.currentTimeMillis() - start) + "ms");
            return cache;
        }


        HouseMaterialSummaryResponse response = new HouseMaterialSummaryResponse();
        response.setHouseId(houseId);

        List<HouseRoom> rooms = houseRoomService.getConfirmedRoomsByHouseId(houseId);

        if (rooms == null) {
            return response;
        }

        // 初始化主材类别
        String[] mainTypes = {"FLOOR", "WALL", "CEILING", "CABINET"};
        for (String type : mainTypes) {
            response.getMainMaterials().put(type, new ArrayList<>());
        }

        // 汇总主材
        for (HouseRoom room : rooms) {

            SchemeRoomMaterial scheme = schemeRoomMaterialService.getByRoomId(room.getId());
            if (scheme == null) {
                System.out.println("⚠️ roomId=" + room.getId() + " 未找到 scheme");
                continue;
            }

            Map<String, BigDecimal> roomTypeAreas = Map.of(
                    "FLOOR", scheme.getFloorArea() != null ? scheme.getFloorArea() : BigDecimal.ZERO,
                    "WALL", scheme.getWallArea() != null ? scheme.getWallArea() : BigDecimal.ZERO,
                    "CEILING", scheme.getCeilingArea() != null ? scheme.getCeilingArea() : BigDecimal.ZERO,
                    "CABINET", scheme.getCabinetArea() != null ? scheme.getCabinetArea() : BigDecimal.ZERO
            );

            Map<String, Object> roomTypeMaterials = new HashMap<>();
            roomTypeMaterials.put("FLOOR", scheme.getFloorMaterial());
            roomTypeMaterials.put("WALL", scheme.getWallMaterial());
            roomTypeMaterials.put("CEILING", scheme.getCeilingMaterial());
            roomTypeMaterials.put("CABINET", scheme.getCabinetMaterial());


            for (String type : mainTypes) {

                Object mat = roomTypeMaterials.get(type);
                BigDecimal area = roomTypeAreas.get(type);

                if (mat == null) {
                    continue;
                }
                if (area == null || area.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                String materialType = String.valueOf(mat);

                List<Material> materialList = materialRepository.findByType(materialType);

                HouseMaterialSummaryResponse.MaterialDetail detail =
                        new HouseMaterialSummaryResponse.MaterialDetail();
                detail.setType(type);
                detail.setDisplayName(
                        materialList != null && !materialList.isEmpty()
                                ? materialList.get(0).getDisplayName()
                                : materialType
                );
                detail.setArea(area);

                response.getMainMaterials().get(type).add(detail);

            }
        }


        List<AuxiliaryMaterial> auxiliaries = auxiliaryMaterialRepository.findAll();

        if (auxiliaries != null) {
            for (AuxiliaryMaterial aux : auxiliaries) {
                try {

                    BigDecimal quantity = calculateAuxiliaryQuantity(aux, houseId);

                    HouseMaterialSummaryResponse.AuxiliaryMaterialItem item =
                            new HouseMaterialSummaryResponse.AuxiliaryMaterialItem();
                    item.setName(aux.getName());
                    item.setCategory(aux.getCategory());
                    item.setUnit(aux.getUnit());
                    item.setQuantity(quantity);
                    item.setRemark(aux.getRemark());

                    response.getAuxiliaryMaterials().add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        redisTemplate.opsForValue().set(cacheKey, response, 10, TimeUnit.MINUTES);

        System.out.println("[HouseMaterials] MISS cache, cost="
                + (System.currentTimeMillis() - start) + "ms");

        return response;
    }


    /**
     * 计算辅材用量
     */
    private BigDecimal calculateAuxiliaryQuantity(AuxiliaryMaterial auxiliary, Long houseId) {
        BigDecimal baseArea = houseService.getHouseWithCalculatedFields(houseId).getArea();
        int roomCount = houseRoomService.getConfirmedRoomsByHouseId(houseId).size();

        return switch (auxiliary.getCategory().toUpperCase()) {
            case "CEMENT" -> baseArea.multiply(new BigDecimal("0.1"));
            case "PIPE" -> new BigDecimal(roomCount * 2);
            case "WIRE" -> baseArea.multiply(new BigDecimal("1.2"));
            case "FIXING_MATERIALS" -> new BigDecimal(roomCount * 5);
            case "ETC" -> calculateEtcMaterialQuantity(auxiliary, baseArea, roomCount);
            default -> BigDecimal.ONE;
        };
    }

    /**
     * 计算 ETC 类别辅材用量
     */
    private BigDecimal calculateEtcMaterialQuantity(AuxiliaryMaterial auxiliary, BigDecimal baseArea, int roomCount) {
        String name = auxiliary.getName().toLowerCase();
        return switch (name) {
            case "螺丝", "螺钉", "膨胀螺栓" -> baseArea.multiply(new BigDecimal("10"));
            case "胶水", "玻璃胶", "密封胶" -> baseArea.multiply(new BigDecimal("0.1"));
            case "保温棉", "隔音棉" -> baseArea.multiply(new BigDecimal("0.5"));
            case "砂纸", "打磨纸" -> new BigDecimal(roomCount * 3);
            case "保护膜", "防尘膜" -> baseArea.multiply(new BigDecimal("0.8"));
            default -> BigDecimal.ONE;
        };
    }


    public HouseStageResponse getStagesByHouse(Long houseId, Long userId) {
        if(!houseService.getHouseById(houseId).getUser().getId().equals(userId)){
            throw new RuntimeException("无权限访问");
        }

        List<Stage> stages = stageRepository.findByHouseIdOrderByOrderAsc(houseId); // 按order升序查询
        HouseStageResponse response = new HouseStageResponse();

        for (Stage stage : stages) {
            HouseStageResponse.StageInfo info = new HouseStageResponse.StageInfo();
            info.setOrder(stage.getOrder());
            info.setStageName(stage.getStageName());
            info.setStatus(STATUS_MAP.getOrDefault(stage.getStatus(), stage.getStatus().toString()));;
            info.setMainWorkerType(WORKER_TYPE_MAP.getOrDefault(stage.getMainWorkerType(), "未知工种"));
            info.setRequiredCount(stage.getRequiredCount());
            info.setEstimatedDay(stage.getEstimatedDay());

            info.setStart_at(stage.getStart_at());
            info.setEnd_at(stage.getEnd_at());

            response.getStages().add(info);
        }

        return response;
    }

    public static final Map<Stage.StageStatus, String> STATUS_MAP = Map.of(
            Stage.StageStatus.PENDING, "待开始",
            Stage.StageStatus.IN_PROGRESS, "进行中",
            Stage.StageStatus.COMPLETED, "已完成",
            Stage.StageStatus.ACCEPTED, "已验收"
    );


    public static final Map<WorkerSkill.WorkerType, String> WORKER_TYPE_MAP = Map.of(
            WorkerSkill.WorkerType.DEMOLITION, "拆改工",
            WorkerSkill.WorkerType.ELECTRICAL, "水电工",
            WorkerSkill.WorkerType.PLASTER, "泥工",
            WorkerSkill.WorkerType.CARPENTRY, "木工",
            WorkerSkill.WorkerType.PAINTING, "油漆工",
            WorkerSkill.WorkerType.TILING, "瓦工",
            WorkerSkill.WorkerType.INSTALLATION, "收尾工"
    );


    public StageDetailResponse getStageDetail(Long houseId, Long userId, Integer order) {
        // 1️⃣ 校验权限
        House house = houseService.getHouseById(houseId);
        if (!house.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权限访问");
        }
        HouseLayout layout = (HouseLayout) houseLayoutRepository.findByHouseIdAndLayoutStatus(houseId, HouseLayout.LayoutStatus.CONFIRMED)
                .orElseThrow(() -> new RuntimeException("无确认布局"));
        HouseLayoutImage designingImageUrl = houseLayoutImageRepository.findByLayoutIdAndImageType(layout.getId(), HouseLayoutImage.ImageType.FINAL);


        // 2️⃣ 获取对应阶段
        Stage stage = (Stage) stageRepository.findByHouseIdAndOrder(houseId, order)
                .orElseThrow(() -> new RuntimeException("阶段不存在"));

        // 3️⃣ 构建返回对象
        StageDetailResponse.StageInfo info = new StageDetailResponse.StageInfo();
        info.setDesigning_image_url(designingImageUrl.getImageUrl());
        info.setOrder(stage.getOrder());
        info.setStageName(stage.getStageName());
        info.setStatus(STATUS_MAP.getOrDefault(stage.getStatus(), stage.getStatus().toString()));
        info.setMainWorkerType(WORKER_TYPE_MAP.getOrDefault(stage.getMainWorkerType(), "未知工种"));info.setRequiredCount(stage.getRequiredCount());
        info.setEstimatedDay(stage.getEstimatedDay());
        info.setExpectedStartAt(String.valueOf(stage.getExpectedStartAt()));
        info.setStart_at(stage.getStart_at());
        info.setEnd_at(stage.getEnd_at());

        // 4️⃣ 填充材料信息（复用已有方法）
        HouseMaterialSummaryResponse allMaterials = calculateHouseMaterials(houseId);

        // 主材
        Map<String, Integer> mainMaterialStageMap = new HashMap<>();
        allMaterials.getMainMaterials().forEach((type, list) -> {
            for (HouseMaterialSummaryResponse.MaterialDetail m : list) {
                // 使用已有规则确定主材派送阶段
                int stageNum = getStageForMaterial(type);
                if (stageNum == order) {
                    HouseStageMaterialsResponse.MaterialInfo matInfo = new HouseStageMaterialsResponse.MaterialInfo();
                    matInfo.setType(m.getType());
                    matInfo.setDisplayName(m.getDisplayName());
                    matInfo.setArea(m.getArea());
                    info.getMainMaterials().add(matInfo);
                }
            }
        });

        // 辅材
        allMaterials.getAuxiliaryMaterials().forEach(aux -> {
            int stageNum = getStageForAuxiliary(aux.getCategory());
            if (stageNum == order) {
                HouseStageMaterialsResponse.AuxMaterialInfo auxInfo = new HouseStageMaterialsResponse.AuxMaterialInfo();
                auxInfo.setName(aux.getName());
                auxInfo.setCategory(aux.getCategory());
                auxInfo.setUnit(aux.getUnit());
                auxInfo.setQuantity(aux.getQuantity());
                info.getAuxiliaryMaterials().add(auxInfo);
            }
        });

        StageDetailResponse response = new StageDetailResponse();
        response.setStageInfo(info);

        StageDetailResponse.WorkerResponse workerResp = new StageDetailResponse.WorkerResponse();

        List<StageAssignment> assignments = stageAssignmentRepository.findByStageId(stage.getId());

        for (StageAssignment assignment : assignments) {
            Worker worker = assignment.getWorker();

            StageDetailResponse.WorkerInfo wi = new StageDetailResponse.WorkerInfo();
            wi.setWorkerId(worker.getUserId());
            wi.setRealName(worker.getRealName());
            wi.setAvatarUrl(worker.getUser().getAvatarUrl());
            wi.setPhone(worker.getUser().getPhone());
            wi.setEmail(worker.getUser().getEmail());

            wi.setExpectedStartAt(assignment.getExpectedStartAt().toLocalDate());
            wi.setExpectedEndAt(assignment.getExpectedEndAt().toLocalDate().minusDays(1));

            // 技能信息（如果一个工人可能有多技能，按主工种取）
            Optional<WorkerSkill> skillOpt = workerSkillRepository.findByWorkerIdAndWorkerType(
                    worker.getUserId(), stage.getMainWorkerType()
            );

            wi.setSkillLevel(
                    skillOpt.map(s -> s.getLevel().name())
                            .orElse("SKILLED") // 默认
            );


            wi.setRating(worker.getRating());

            workerResp.getWorkers().add(wi);
        }

        response.setWorkerResponse(workerResp);
        response.setDecorationType(house.getDecorationType());



        return response;
    }

    @Transactional
    public void StartStage(Long userId, Long houseId, Integer order) {
        House house = houseService.getHouseById(houseId);
        if(!house.getUser().getId().equals(userId)){
            throw new RuntimeException("无操作权限");
        }
        Stage stage = (Stage) stageRepository.findByHouseIdAndOrder(houseId, order)
                .orElseThrow(() -> new RuntimeException("阶段不存在"));
        stage.setStatus(Stage.StageStatus.IN_PROGRESS);
        stage.setStart_at(LocalDateTime.now());
        stageRepository.save(stage);

        List<StageAssignment> assignments = stageAssignmentRepository.findByStageId(stage.getId());

        for (StageAssignment assignment : assignments) {
            assignment.setStatus(StageAssignment.AssignmentStatus.IN_PROGRESS);
            assignment.setStartAt(LocalDateTime.now());
        }
        stageAssignmentRepository.saveAll(assignments);
    }

    @Transactional
    public void CompleteStage(Long stageId) {
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new RuntimeException("阶段不存在"));
        stage.setStatus(Stage.StageStatus.COMPLETED);
        stage.setEnd_at(LocalDateTime.now());
        stageRepository.save(stage);

        List<StageAssignment> assignments = stageAssignmentRepository.findByStageId(stage.getId());
        for (StageAssignment assignment : assignments) {
            assignment.setStatus(StageAssignment.AssignmentStatus.COMPLETED);
            LocalDateTime now = LocalDateTime.now();
            if(assignment.getExpectedEndAt().isBefore(now)){
                assignment.setEndAt(assignment.getExpectedEndAt());
            }else {
                assignment.setEndAt(now);
            }
        }
        stageAssignmentRepository.saveAll(assignments);
    }


    @Transactional
    public void AcceptStage(Long userId, Long houseId, Integer order) {
        House house = houseService.getHouseById(houseId);
        if(!house.getUser().getId().equals(userId)){
            throw new RuntimeException("无操作权限");
        }
        Stage stage = (Stage) stageRepository.findByHouseIdAndOrder(houseId, order)
                .orElseThrow(() -> new RuntimeException("阶段不存在"));
        stage.setStatus(Stage.StageStatus.ACCEPTED);
        stageRepository.save(stage);
    }
}
