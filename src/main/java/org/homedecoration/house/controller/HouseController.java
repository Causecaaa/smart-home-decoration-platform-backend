package org.homedecoration.house.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.house.dto.request.CreateHouseRequest;
import org.homedecoration.house.dto.request.UpdateHouseRequest;
import org.homedecoration.house.dto.response.HouseMaterialSummaryResponse;
import org.homedecoration.house.dto.response.HouseQuotationResponse;
import org.homedecoration.house.dto.response.HouseResponse;
import org.homedecoration.house.service.HouseService;
import org.homedecoration.house.service.HouseQuotationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/house")
public class HouseController {

    private final HouseService houseService;
    private final JwtUtil jwtUtil;
    private final HouseQuotationService houseQuotationService;

    public HouseController(HouseService houseService, JwtUtil jwtUtil, HouseQuotationService houseQuotationService) {
        this.houseService = houseService;
        this.jwtUtil = jwtUtil;
        this.houseQuotationService = houseQuotationService;
    }


    // 新增房屋
    @PostMapping("/create-house")
    public ApiResponse<HouseResponse> createHouse(
            @RequestBody @Valid CreateHouseRequest request,
            HttpServletRequest httpRequest) {

        Long userId = jwtUtil.getUserId(httpRequest);
        var house = houseService.createHouse(request, userId);

        return ApiResponse.success(
                HouseResponse.toDTO(house)
        );
    }

    // 当前用户房屋
    @GetMapping("/get-all")
    public ApiResponse<List<HouseResponse>> getAllHousesByUserId(HttpServletRequest httpRequest) {

        Long userId = jwtUtil.getUserId(httpRequest);

        List<HouseResponse> list = houseService.getAllHousesByUserId(userId)
                .stream()
                .map(HouseResponse::toDTO)
                .toList();

        return ApiResponse.success(list);
    }

    // 管理员查看
    @GetMapping("/{userId}/get-all")
    public ApiResponse<List<HouseResponse>> getAllHousesByUserId(@PathVariable Long userId) {

        List<HouseResponse> list = houseService.getAllHousesByUserId(userId)
                .stream()
                .map(house -> HouseResponse.toDTO(house))
                .toList();

        return ApiResponse.success(list);
    }

    // 查询单个
    @GetMapping("/{houseId}/find")
    public ApiResponse<HouseResponse> getHouseById(@PathVariable Long houseId) {

        var house = houseService.getHouseWithCalculatedFields(houseId);

        return ApiResponse.success(
                HouseResponse.toDTO(house)
        );
    }

    // 更新
    @PutMapping("/{houseId}/update")
    public ApiResponse<HouseResponse> updateHouse(
            @PathVariable Long houseId,
            @Valid @RequestBody UpdateHouseRequest request,
            HttpServletRequest httpRequest) {

        Long userId = jwtUtil.getUserId(httpRequest);
        var house = houseService.updateHouse(request, houseId, userId);

        return ApiResponse.success(
                HouseResponse.toDTO(house)
        );
    }

    // 删除
    @DeleteMapping("/{houseId}")
    public ApiResponse<String> deleteHouse(
            @PathVariable Long houseId,
            HttpServletRequest httpRequest) {

        Long userId = jwtUtil.getUserId(httpRequest);
        houseService.deleteHouse(houseId, userId);
        return ApiResponse.success("删除成功");
    }

    @GetMapping("/quotation/{houseId}")
    public ApiResponse<HouseQuotationResponse> getHouseQuotation(
            @PathVariable Long houseId,
            HttpServletRequest httpRequest) {
        HouseQuotationResponse quote = houseQuotationService.calculateHouseQuotation(houseId, jwtUtil.getUserId(httpRequest));
        return ApiResponse.success(quote);
    }
}
