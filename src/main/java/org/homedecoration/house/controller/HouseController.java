package org.homedecoration.house.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.house.dto.request.CreateHouseRequest;
import org.homedecoration.house.dto.request.UpdateHouseRequest;
import org.homedecoration.house.dto.response.HouseResponse;
import org.homedecoration.house.service.HouseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/house")
public class HouseController {

    private final HouseService houseService;
    private final JwtUtil jwtUtil;

    public HouseController(HouseService houseService, JwtUtil jwtUtil) {
        this.houseService = houseService;
        this.jwtUtil = jwtUtil;
    }

    // 新增房屋
    @PostMapping("/create-house")
    public ApiResponse<HouseResponse> createHouse(
            @RequestBody @Valid CreateHouseRequest request,
            HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(
                HouseResponse.toDTO(houseService.createHouse(request, userId))
        );
    }


    // 查询当前用户所有房屋
    @GetMapping("/get-all")
    public ApiResponse<List<HouseResponse>> getAllHousesByUserId(HttpServletRequest httpRequest) {
        List<HouseResponse> list = houseService.getAllHousesByUserId(jwtUtil.getUserId(httpRequest))
                .stream()
                .map(HouseResponse::toDTO)
                .toList();

        return ApiResponse.success(list);
    }

    // 查询用户所有房屋
    @GetMapping("/{userId}/get-all")
    public ApiResponse<List<HouseResponse>> getAllHousesByUserId(@PathVariable Long userId) {
        List<HouseResponse> list = houseService.getAllHousesByUserId(userId)
                .stream()
                .map(HouseResponse::toDTO)
                .toList();

        return ApiResponse.success(list);
    }


    // 查询单个房屋
    @GetMapping("/{houseId}/find")
    public ApiResponse<HouseResponse> getHouseById(@PathVariable Long houseId) {
        return ApiResponse.success(
                HouseResponse.toDTO(houseService.getHouseById(houseId))
        );
    }

    // 更新房屋
    @PutMapping("/{houseId}/update")
    public ApiResponse<HouseResponse> updateHouse(@PathVariable Long houseId, @Valid @RequestBody UpdateHouseRequest request,
                                                  HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(
                HouseResponse.toDTO(houseService.updateHouse(request, houseId, userId))
        );
    }


    // 删除房屋
    @DeleteMapping("/{houseId}")
    public ApiResponse<String> deleteHouse(@PathVariable Long houseId,HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserId(httpRequest);
        houseService.deleteHouse(houseId, userId);
        return ApiResponse.success("删除成功");
    }
}
