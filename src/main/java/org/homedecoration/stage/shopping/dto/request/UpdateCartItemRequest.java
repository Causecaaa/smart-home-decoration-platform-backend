package org.homedecoration.stage.shopping.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateCartItemRequest {

    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "0.0", message = "数量不能小于0")
    private BigDecimal quantity;
}
