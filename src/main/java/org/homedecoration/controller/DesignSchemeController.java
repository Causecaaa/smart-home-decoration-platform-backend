package org.homedecoration.controller;

import org.homedecoration.service.DesignSchemeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/design-scheme")
public class DesignSchemeController {

    private final DesignSchemeService designSchemeService;

    public DesignSchemeController(DesignSchemeService designSchemeService) {
        this.designSchemeService = designSchemeService;
    }

}
