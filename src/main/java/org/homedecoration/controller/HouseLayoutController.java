package org.homedecoration.controller;

import org.homedecoration.service.HouseLayoutService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/house-layout")
public class HouseLayoutController {

    private final HouseLayoutService houseLayoutService;

    public HouseLayoutController(HouseLayoutService houseLayoutService) {
        this.houseLayoutService = houseLayoutService;
    }

}
