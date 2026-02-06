package org.homedecoration.stage.shopping.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductRequest {

    @NotBlank(message = "商品名称不能为空")
    private String name;

    @NotNull(message = "材料类型不能为空")
    private Integer materialType;

    private String mainCategory;

    private String subCategory;

    private String brand;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.0", message = "价格不能小于0")
    private BigDecimal price;

    private String unit;

    private Integer stock;

    private String description;

    private Integer hasSpec;

    private Integer status;
}
