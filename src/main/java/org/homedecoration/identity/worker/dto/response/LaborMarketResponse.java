package org.homedecoration.identity.worker.dto.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LaborMarketResponse {
    private Long stageId;
    private String mainWorkerType; // 安装/木工/油漆...
    private Integer requiredCount; // 所需人数
    private String expectedStartAt;
    private Integer estimatedDay;  // 预计天数
    private Boolean canEdit;

    private Integer page;
    private Integer size;
    private Integer totalPages;
    private Long totalElements;

    private Integer page;
    private Integer size;
    private Integer totalPages;
    private Long totalElements;

    private List<WorkerResponse> workers = new ArrayList<>();
}