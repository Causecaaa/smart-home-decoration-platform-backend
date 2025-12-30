package org.homedecoration.controller;

import org.homedecoration.service.HouseLayoutImageService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/house-layout-image")
public class HouseLayoutImageController {

    private final HouseLayoutImageService houseLayoutImageService;

    public HouseLayoutImageController(HouseLayoutImageService houseLayoutImageService) {
        this.houseLayoutImageService = houseLayoutImageService;
    }

}
