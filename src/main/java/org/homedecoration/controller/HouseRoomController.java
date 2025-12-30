package org.homedecoration.controller;

import org.homedecoration.service.HouseRoomService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/house-room")
public class HouseRoomController {

    private final HouseRoomService houseRoomService;

    public HouseRoomController(HouseRoomService houseRoomService) {
        this.houseRoomService = houseRoomService;
    }

}
