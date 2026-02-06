package org.homedecoration.stage.shopping.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StageOrderItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}
