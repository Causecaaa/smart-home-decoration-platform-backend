package org.homedecoration.controller;

import org.homedecoration.entity.House;
import org.homedecoration.entity.User;
import org.homedecoration.service.HouseService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/house")
public class HouseController {

    private final HouseService houseService;

    public HouseController(HouseService houseService) {
        this.houseService = houseService;
    }

    // 新增房屋
    @PostMapping("/create")
    public House createHouse(@RequestBody House house) {
        return houseService.createHouse(house);
    }


    // 查询当前用户所有房屋
    @GetMapping("/get-all")
    public List<House> getAllHouses() {
        return houseService.getAllHouses();
    }

    // 查询单个房屋
    @GetMapping("/find/{id}")
    public House getHouseById(@PathVariable Long id) {
        return houseService.getHouseById(id);
    }

    // 更新房屋
    @PutMapping("/update/{id}")
    public House updateHouse(@PathVariable Long id, @RequestBody House house) {
        return houseService.updateHouse(id, house);
    }

    // 删除房屋
    @DeleteMapping("/{id}")
    public void deleteHouse(@PathVariable Long id) {
        houseService.deleteHouse(id);
    }
}
