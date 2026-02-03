package org.homedecoration.stage.stage.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class StageDetailResponse {
    private StageInfo stageInfo;


    @Data
    public static class StageInfo {
        private Integer order;      // 1~7
        private String stageName;   // 阶段名称
        private String status;      // PENDING, IN_PROGRESS, FINISHED...
        private String mainWorkerType; // 安装/木工/油漆...
        private Integer requiredCount; // 所需人数
        private String expectedStartAt;
        private Integer estimatedDay;  // 预计天数

        private LocalDateTime start_at;
        private LocalDateTime end_at;

        private List<HouseStageMaterialsResponse.MaterialInfo> mainMaterials = new ArrayList<>();
        private List<HouseStageMaterialsResponse.AuxMaterialInfo> auxiliaryMaterials = new ArrayList<>();

    }

}
