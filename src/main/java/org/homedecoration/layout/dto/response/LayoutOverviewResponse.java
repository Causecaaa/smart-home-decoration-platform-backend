package org.homedecoration.layout.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class LayoutOverviewResponse {

    private DraftLayoutResponse draftLayout;

    private List<LayoutDesignerResponse> designerLayouts;

    private LayoutDesignerResponse keepOriginalLayout;
}
