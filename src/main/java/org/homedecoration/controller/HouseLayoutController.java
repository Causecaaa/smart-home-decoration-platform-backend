package org.homedecoration.controller;

import org.homedecoration.dto.request.CreateUserLayoutRequest;
import org.homedecoration.dto.response.HouseLayoutResponse;
import org.homedecoration.entity.HouseLayout;
import org.homedecoration.service.HouseLayoutService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/house-layout")
public class HouseLayoutController {

    private final HouseLayoutService houseLayoutService;

    public HouseLayoutController(HouseLayoutService houseLayoutService) {
        this.houseLayoutService = houseLayoutService;
    }

    @PostMapping("/create-by-user")
    public HouseLayoutResponse createByUser(@RequestBody CreateUserLayoutRequest dto) {

        HouseLayout layout = houseLayoutService.createByUser(dto);

        return HouseLayoutResponse.toDTO(layout);
    }


}
