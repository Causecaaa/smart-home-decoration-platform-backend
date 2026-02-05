package org.homedecoration.identity.worker.dto.response;

import lombok.Data;
import org.homedecoration.stage.assignment.entity.StageAssignment;
import org.homedecoration.stage.stage.dto.response.HouseStageMaterialsResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class WorkerStageCalendarResponse {

    private List<StageAssignmentItem> assignments;
    private List<LocalDate> LeaveDays;

    @Data
    public static class StageAssignmentItem {
        private Long assignmentId;
        private LocalDate expected_Start_at;  // 阶段开始
        private LocalDate expected_End_at;    // 阶段结束
        private StageAssignment.AssignmentStatus status;

        private Long stageId;
        private String stageName;

        private Long houseId;
        private String city;
        private String communityName;
        private String buildingNo;
        private String unitNo;
        private String roomNo;
        private BigDecimal area;


        private List<WorkerSimpleResponse> coworkers;

        private List<HouseStageMaterialsResponse.MaterialInfo> mainMaterials = new ArrayList<>();
        private List<HouseStageMaterialsResponse.AuxMaterialInfo> auxiliaryMaterials = new ArrayList<>();
    }

}
