package org.homedecoration.stage.review.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.stage.review.dto.request.CreateStageReviewRequest;
import org.homedecoration.stage.review.dto.response.StageReviewResponse;
import org.homedecoration.stage.review.service.StageReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stage-review")
public class StageReviewController {

    private final StageReviewService stageReviewService;
    private final JwtUtil jwtUtil;

    @PostMapping("/stage/{stageId}")
    public ApiResponse<List<StageReviewResponse>> createStageReview(
            @PathVariable Long stageId,
            @RequestBody @Valid CreateStageReviewRequest request,
            HttpServletRequest httpRequest
    ) {
        Long reviewerId = jwtUtil.getUserId(httpRequest);

        List<StageReviewResponse> reviews = stageReviewService.createStageReview(stageId, request, reviewerId)
                .stream()
                .map(StageReviewResponse::toDTO)
                .toList();
        return ApiResponse.success(reviews);
    }

    @GetMapping("/stage/{stageId}")
    public ApiResponse<List<StageReviewResponse>> listByStage(@PathVariable Long stageId) {
        List<StageReviewResponse> reviews = stageReviewService.listByStageId(stageId)
                .stream()
                .map(StageReviewResponse::toDTO)
                .toList();
        return ApiResponse.success(reviews);
    }
}
