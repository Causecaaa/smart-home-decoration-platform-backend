package org.homedecoration.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.homedecoration.common.ApiResponse;
import org.homedecoration.config.JwtUtil;
import org.homedecoration.dto.request.CreateLayoutRequest;
import org.homedecoration.dto.response.HouseLayoutResponse;
import org.homedecoration.service.HouseLayoutService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/house-layout")
public class HouseLayoutController {

    private final HouseLayoutService houseLayoutService;
    private final JwtUtil jwtUtil;

    public HouseLayoutController(HouseLayoutService houseLayoutService, JwtUtil jwtUtil) {
        this.houseLayoutService = houseLayoutService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/create-draft")
    public ApiResponse<HouseLayoutResponse> createDraft(@RequestBody CreateLayoutRequest request) {
        return ApiResponse.success(
                HouseLayoutResponse.toDTO(houseLayoutService.createDraft(request))
        );
    }

    @PostMapping("/create-layout")
    public ApiResponse<HouseLayoutResponse> createLayout(
            @RequestBody @Valid CreateLayoutRequest request,
            HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").replace("Bearer ", "");
        request.setUserId(jwtUtil.getUserId(token));
        return ApiResponse.success(
                HouseLayoutResponse.toDTO(
                        houseLayoutService.createLayout(request)
                )
        );
    }


}
