package org.homedecoration.bill.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.homedecoration.bill.dto.request.CreateBillRequest;
import org.homedecoration.bill.dto.response.BillResponse;
import org.homedecoration.bill.entity.Bill;
import org.homedecoration.bill.service.BillService;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bill")
public class BillController {

    private final BillService billService;
    private final JwtUtil jwtUtil;

    public BillController(BillService billService, JwtUtil jwtUtil) {
        this.billService = billService;
        this.jwtUtil = jwtUtil;
    }

    // 新建账单
    @PostMapping("/create")
    public ApiResponse<BillResponse> createBill(
            HttpServletRequest httpRequest,
            @Valid @RequestBody CreateBillRequest request
    ) {
        Long payerId = jwtUtil.getUserId(httpRequest); // 支付方从 token 获取
        BillResponse response = billService.createBill(request, payerId);
        return ApiResponse.success(response);
    }


    @GetMapping("/{BillId}/get")
    public ApiResponse<BillResponse> getBill(@Valid @PathVariable Long BillId) {
        return ApiResponse.success(
                BillResponse.toDTO(billService.getBill(BillId))
        );
    }

    @PostMapping("/pay/deposit/{id}")
    public ApiResponse<Bill> payDeposit(HttpServletRequest request, @PathVariable Long id) {
        Long payerId = jwtUtil.getUserId(request);
        return ApiResponse.success(
                billService.payDeposit(payerId, id)
        );
    }

    @PostMapping("/pay/final/{id}")
    public ApiResponse<Bill> payFinal(HttpServletRequest request, @PathVariable Long id) {
        Long payerId = jwtUtil.getUserId(request);
        return ApiResponse.success(
                billService.payFinal(payerId, id)
        );
    }
}
