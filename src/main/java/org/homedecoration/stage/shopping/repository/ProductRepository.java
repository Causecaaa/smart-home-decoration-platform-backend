package org.homedecoration.stage.shopping.repository;

import org.homedecoration.stage.shopping.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    List<Product> findByStatus(Integer status);

    List<Product> findByMaterialTypeAndStatus(Integer materialType, Integer status);
}

