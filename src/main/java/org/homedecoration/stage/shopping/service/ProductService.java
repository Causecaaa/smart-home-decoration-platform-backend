package org.homedecoration.stage.shopping.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.exception.BusinessException;
import org.homedecoration.stage.shopping.dto.request.CreateProductRequest;
import org.homedecoration.stage.shopping.entity.Product;
import org.homedecoration.stage.shopping.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;


import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product create(CreateProductRequest request) {
        Product product = new Product();
        fillProduct(product, request);
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long productId, CreateProductRequest request) {
        Product product = getById(productId);
        fillProduct(product, request);
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Page<Product> list(
            Integer materialType,
            String mainCategory,
            String subCategory,
            String keyword,
            Pageable pageable
    ) {
        return productRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 只查上架商品
            predicates.add(cb.equal(root.get("status"), 1));

            if (materialType != null) {
                predicates.add(cb.equal(root.get("materialType"), materialType));
            }

            if (mainCategory != null && !mainCategory.isBlank()) {
                predicates.add(cb.equal(root.get("mainCategory"), mainCategory));
            }

            if (subCategory != null && !subCategory.isBlank()) {
                predicates.add(cb.equal(root.get("subCategory"), subCategory));
            }

            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(root.get("name"), "%" + keyword + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable); // ✅ 这里传 pageable
    }


    @Transactional(readOnly = true)
    public Product getById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("商品不存在"));
    }

    @Transactional
    public Product changeStatus(Long productId, Integer status) {
        Product product = getById(productId);
        product.setStatus(status);
        return productRepository.save(product);
    }

    private void fillProduct(Product product, CreateProductRequest request) {
        product.setName(request.getName());
        product.setMaterialType(request.getMaterialType());
        product.setMainCategory(request.getMainCategory());
        product.setSubCategory(request.getSubCategory());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setUnit(request.getUnit());
        product.setStock(request.getStock() == null ? 0 : request.getStock());
        product.setDescription(request.getDescription());
        product.setHasSpec(request.getHasSpec() == null ? 0 : request.getHasSpec());
        product.setStatus(request.getStatus() == null ? 1 : request.getStatus());
    }
}
