package org.homedecoration.stage.shopping.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.stage.shopping.dto.response.StageOrderResponse;
import org.homedecoration.stage.shopping.dto.response.StagePurchasedMaterialsResponse;
import org.homedecoration.stage.shopping.entity.StageOrder;
import org.homedecoration.stage.shopping.service.StageOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stage-order")
public class StageOrderController {

    private final StageOrderService stageOrderService;
    private final JwtUtil jwtUtil;

    @PostMapping("/{stageId}/checkout")
    public ApiResponse<StageOrderResponse> checkout(@PathVariable Long stageId,
                                                    HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(stageOrderService.checkout(stageId, userId));
    }

    @PatchMapping("/{orderId}/pay-order")
    public ApiResponse<StageOrderResponse> payOrder(@PathVariable Long orderId,
                                                    HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(stageOrderService.updateStatus(orderId, userId, StageOrder.OrderStatus.PAID));
    }

    @GetMapping("/{stageId}/summary")
    public ApiResponse<StagePurchasedMaterialsResponse> getPurchasedMaterials(
            @PathVariable Long stageId,
            HttpServletRequest httpRequest) {

        Long userId = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(stageOrderService.getPurchasedMaterials(stageId, userId));
    }



    @GetMapping("/my")
    public ApiResponse<List<StageOrderResponse>> myOrders(HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(stageOrderService.myOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<StageOrderResponse> getById(@PathVariable Long orderId,
                                                   HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(stageOrderService.getById(orderId, userId));
    }

    @PatchMapping("/{orderId}/status")
    public ApiResponse<StageOrderResponse> updateStatus(@PathVariable Long orderId,
                                                        @RequestParam StageOrder.OrderStatus status,
                                                        HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(stageOrderService.updateStatus(orderId, userId, status));
    }
}
