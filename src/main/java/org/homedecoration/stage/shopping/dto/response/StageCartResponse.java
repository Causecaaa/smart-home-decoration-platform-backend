package org.homedecoration.stage.shopping.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class StageCartResponse {

    private Long cartId;
    private Long stageId;
    private Long userId;
    private List<StageCartItemResponse> items;
    private BigDecimal totalAmount;
}
