package org.homedecoration.furnitureScheme.service;

import org.homedecoration.furnitureScheme.entity.FurnitureScheme;
import org.homedecoration.furnitureScheme.repository.FurnitureSchemeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FurnitureSchemeService {

    private final FurnitureSchemeRepository furnitureSchemeRepository;

    public FurnitureSchemeService(FurnitureSchemeRepository furnitureSchemeRepository) {
        this.furnitureSchemeRepository = furnitureSchemeRepository;
    }

    public List<FurnitureScheme> findByLayoutId(Long layoutId) {
        return null;
    }

    public FurnitureScheme save(FurnitureScheme scheme) {
        return null;
    }
}
