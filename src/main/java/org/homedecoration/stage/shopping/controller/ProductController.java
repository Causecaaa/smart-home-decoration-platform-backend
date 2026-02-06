package org.homedecoration.stage.shopping.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.stage.shopping.dto.request.CreateProductRequest;
import org.homedecoration.stage.shopping.dto.response.ProductResponse;
import org.homedecoration.stage.shopping.entity.Product;
import org.homedecoration.stage.shopping.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;


@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/list")
    public ApiResponse<Page<ProductResponse>> list(
            @RequestParam(required = false) Integer materialType,
            @RequestParam(required = false) String mainCategory,
            @RequestParam(required = false) String subCategory,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page, // 0-base
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size); // 直接用 page
        Page<Product> productPage = productService.list(materialType, mainCategory, subCategory, keyword, pageable);
        Page<ProductResponse> dtoPage = productPage.map(ProductResponse::toDTO);
        return ApiResponse.success(dtoPage);
    }





    @PostMapping("/create")
    public ApiResponse<ProductResponse> create(@RequestBody @Valid CreateProductRequest request) {
        return ApiResponse.success(ProductResponse.toDTO(productService.create(request)));
    }

    @PutMapping("/{productId}/update")
    public ApiResponse<ProductResponse> update(@PathVariable Long productId,
                                               @RequestBody @Valid CreateProductRequest request) {
        return ApiResponse.success(ProductResponse.toDTO(productService.update(productId, request)));
    }


    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getById(@PathVariable Long productId) {
        return ApiResponse.success(ProductResponse.toDTO(productService.getById(productId)));
    }

    @PatchMapping("/{productId}/status")
    public ApiResponse<ProductResponse> changeStatus(@PathVariable Long productId,
                                                     @RequestParam Integer status) {
        return ApiResponse.success(ProductResponse.toDTO(productService.changeStatus(productId, status)));
    }
}
