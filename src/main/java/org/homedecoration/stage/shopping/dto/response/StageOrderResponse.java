package org.homedecoration.stage.shopping.dto.response;

import lombok.Data;
import org.homedecoration.stage.shopping.entity.StageOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StageOrderResponse {

    private Long id;
    private Long stageId;
    private Long userId;
    private BigDecimal totalAmount;
    private StageOrder.OrderStatus status;
    private LocalDateTime createdAt;
    private List<StageOrderItemResponse> items;
}
