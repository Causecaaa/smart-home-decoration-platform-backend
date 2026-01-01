package org.homedecoration.controller;

import jakarta.validation.Valid;
import org.homedecoration.common.ApiResponse;
import org.homedecoration.dto.request.UpdateHouseRequest;
import org.homedecoration.dto.response.HouseResponse;
import org.homedecoration.entity.House;
import org.homedecoration.service.HouseService;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse<HouseResponse> createHouse(@Valid @RequestBody House house) {
        return ApiResponse.success(
                HouseResponse.toDTO(houseService.createHouse(house))
        );
    }


    // 查询当前用户所有房屋
    @GetMapping("/get-all")
    public ApiResponse<List<HouseResponse>> getAllHouses() {
        List<HouseResponse> list = houseService.getAllHouses()
                .stream()
                .map(HouseResponse::toDTO)
                .toList();
        return ApiResponse.success(list);
    }

    // 查询单个房屋
    @GetMapping("/find/{id}")
    public ApiResponse<HouseResponse> getHouseById(@PathVariable Long id) {
        return ApiResponse.success(
                HouseResponse.toDTO(houseService.getHouseById(id))
        );
    }

    // 更新房屋
    @PutMapping("/update/{id}")
    public ApiResponse<HouseResponse> updateHouse(@PathVariable Long id, @Valid @RequestBody UpdateHouseRequest request) {
        return ApiResponse.success(
                HouseResponse.toDTO(houseService.updateHouse(request,id))
        );
    }


    // 删除房屋
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteHouse(@PathVariable Long id) {
        houseService.deleteHouse(id);
        return ApiResponse.success("删除成功");
    }
}
