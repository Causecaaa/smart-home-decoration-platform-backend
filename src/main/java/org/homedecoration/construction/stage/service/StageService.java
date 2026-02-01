package org.homedecoration.construction.stage.service;

import lombok.RequiredArgsConstructor;
import org.homedecoration.construction.stage.dto.response.HouseStageMaterialsResponse;
import org.homedecoration.house.dto.response.HouseMaterialSummaryResponse;
import org.homedecoration.house.service.HouseQuotationService;
import org.homedecoration.house.service.HouseService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StageService {

    private final HouseQuotationService houseQuotationService;
    private final HouseService houseService;

    private static final Map<Integer, String> STAGE_MAP = Map.of(
            1, "拆改阶段",
            2, "水电改造",
            3, "泥工阶段",
            4, "木工阶段",
            5, "油漆 / 面漆阶段",
            6, "瓦工 / 瓷砖铺贴",
            7, "安装阶段",
            8, "收尾 / 清洁阶段"
    );

    public HouseStageMaterialsResponse getHouseMaterialsByStage(Long houseId, Long userId) {
        if(!houseService.getHouseById(houseId).getUser().getId().equals(userId)){
            throw new RuntimeException("无权限访问");
        }

        HouseMaterialSummaryResponse allMaterials = houseQuotationService.calculateHouseMaterials(houseId);

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

        // 3️⃣ 构建阶段列表
        for (Map.Entry<Integer, String> stageEntry : STAGE_MAP.entrySet()) {
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

        return response;
    }

    // 主材派送阶段规则
    private int getStageForMaterial(String type) {
        return switch (type) {
            case "FLOOR" -> 2;                 // 水电改造阶段用地面
            case "CABINET" -> 4;               // 木工阶段做柜子
            case "WALL", "CEILING" -> 5;       // 油漆阶段
            default -> 6;                      // 瓦工/其他
        };
    }

    // 辅材派送阶段规则
    private int getStageForAuxiliary(String category) {
        return switch (category) {
            case "PIPE", "WIRE" -> 2;
            case "CEMENT", "FIXING_MATERIALS" -> 3;
            case "ETC" -> 4; // ETC 小件提前到第一次使用阶段
            default -> 6;
        };
    }
}
