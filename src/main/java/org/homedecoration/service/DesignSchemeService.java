package org.homedecoration.service;

import org.homedecoration.entity.DesignScheme;
import org.homedecoration.repository.DesignSchemeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DesignSchemeService {

    private final DesignSchemeRepository designSchemeRepository;

    public DesignSchemeService(DesignSchemeRepository designSchemeRepository) {
        this.designSchemeRepository = designSchemeRepository;
    }

    public List<DesignScheme> findByLayoutId(Long layoutId) {
        return null;
    }

    public DesignScheme save(DesignScheme scheme) {
        return null;
    }
}
