package org.homedecoration.layout.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class LayoutOverviewResponse {

    // 当前进行中的 redesign（可能为 null）
    private CurrentLayoutResponse currentLayout;

    // 历史方案列表（一定是 list）
    private List<LayoutHistoryItemResponse> historyLayouts;
}
