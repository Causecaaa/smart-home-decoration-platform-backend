package org.homedecoration.stage.shopping.dto.response;

import lombok.Data;
import org.homedecoration.stage.shopping.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponse {

    private Long productId;
    private String name;
    private Integer materialType;
    private String mainCategory;
    private String subCategory;
    private String brand;
    private BigDecimal price;
    private String unit;
    private Integer stock;
    private String description;
    private Integer hasSpec;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductResponse toDTO(Product product) {
        ProductResponse response = new ProductResponse();
        response.setProductId(product.getProductId());
        response.setName(product.getName());
        response.setMaterialType(product.getMaterialType());
        response.setMainCategory(product.getMainCategory());
        response.setSubCategory(product.getSubCategory());
        response.setBrand(product.getBrand());
        response.setPrice(product.getPrice());
        response.setUnit(product.getUnit());
        response.setStock(product.getStock());
        response.setDescription(product.getDescription());
        response.setHasSpec(product.getHasSpec());
        response.setStatus(product.getStatus());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }
}
