package org.homedecoration.material.auxiliary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuxiliaryMaterialRepository extends JpaRepository<AuxiliaryMaterial, Long> {
    List<AuxiliaryMaterial> findByCategory(String category);
}