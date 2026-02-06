package org.homedecoration.stage.shopping.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StageCartItemResponse {

    private Long id;
    private Long productId;
    private String brand;
    private String image_url;
    private String productName;
    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal subtotal;
}
