package org.homedecoration.stage.shopping.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.stage.shopping.dto.request.AddCartItemRequest;
import org.homedecoration.stage.shopping.dto.request.UpdateCartItemRequest;
import org.homedecoration.stage.shopping.dto.response.StageCartResponse;
import org.homedecoration.stage.shopping.service.StageCartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stage-cart")
public class StageCartController {

    private final StageCartService stageCartService;
    private final JwtUtil jwtUtil;

    @PostMapping("/{stageId}/item")
    public ApiResponse<StageCartResponse> addItem(@PathVariable Long stageId,
                                                  @RequestBody @Valid AddCartItemRequest request,
                                                  HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(stageCartService.addItem(stageId, userId, request));
    }

    @PutMapping("/{stageId}/item/{itemId}")
    public ApiResponse<StageCartResponse> updateItem(@PathVariable Long stageId,
                                                     @PathVariable Long itemId,
                                                     @RequestBody @Valid UpdateCartItemRequest request,
                                                     HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(stageCartService.updateItem(stageId, userId, itemId, request));
    }

    @DeleteMapping("/{stageId}/item/{itemId}")
    public ApiResponse<StageCartResponse> removeItem(@PathVariable Long stageId,
                                                     @PathVariable Long itemId,
                                                     HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(stageCartService.removeItem(stageId, userId, itemId));
    }

    @GetMapping("/{stageId}")
    public ApiResponse<StageCartResponse> getCart(@PathVariable Long stageId,
                                                  HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(stageCartService.getCart(stageId, userId));
    }
}
