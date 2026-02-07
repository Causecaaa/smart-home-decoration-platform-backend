package org.homedecoration.stage.stage.dto.response;

import lombok.Data;
import org.homedecoration.house.entity.House;
import org.homedecoration.stage.shopping.dto.response.StagePurchasedMaterialsResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class StageDetailResponse {
    private House.DecorationType decorationType;
    private StageInfo stageInfo;
    private WorkerResponse workerResponse;


    @Data
    public static class StageInfo {
        private Long stageId;
        private Integer order;      // 1~7
        private String stageName;   // 阶段名称
        private String status;      // PENDING, IN_PROGRESS, FINISHED...
        private String mainWorkerType; // 安装/木工/油漆...
        private Integer requiredCount; // 所需人数
        private String expectedStartAt;
        private Integer estimatedDay;  // 预计天数


        private LocalDateTime start_at;
        private LocalDateTime end_at;

        private String designing_image_url;

        private List<String> recommendedMaterialTypes;

        private List<HouseStageMaterialsResponse.MaterialInfo> mainMaterials = new ArrayList<>();
        private List<StagePurchasedMaterialsResponse.MainMaterial> purchasedMainMaterials = new ArrayList<>();

        private List<HouseStageMaterialsResponse.AuxMaterialInfo> auxiliaryMaterials = new ArrayList<>();
        private List<StagePurchasedMaterialsResponse.AuxMaterial> purchasedAuxiliaryMaterials = new ArrayList<>();
    }

    @Data
    public static class WorkerResponse {
        private List<WorkerInfo> workers = new ArrayList<>();
    }

    @Data
    public static class WorkerInfo {
        private Long workerId;           // 工人ID
        private String realName;         // 真实姓名
        private String avatarUrl;        // 用户头像
        private String phone;
        private String email;
        private String skillLevel;       // 熟练度：JUNIOR / SKILLED / MASTER
        private BigDecimal rating;           // 对外评分
        private LocalDate expectedStartAt;
        private LocalDate expectedEndAt;
    }

}
