package org.homedecoration.stage.review.dto.response;

import lombok.Data;
import org.homedecoration.stage.review.entity.StageReview;

import java.time.LocalDateTime;

@Data
public class StageReviewResponse {
    private Long id;
    private Long stageId;
    private Long assignmentId;
    private Long reviewerId;
    private Double rating;
    private String comment;
    private LocalDateTime createdAt;

    public static StageReviewResponse toDTO(StageReview stageReview) {
        StageReviewResponse response = new StageReviewResponse();
        response.setId(stageReview.getId());
        response.setStageId(stageReview.getStageId());
        response.setAssignmentId(stageReview.getAssignmentId());
        response.setReviewerId(stageReview.getReviewerId());
        response.setRating(stageReview.getRating());
        response.setComment(stageReview.getComment());
        response.setCreatedAt(stageReview.getCreatedAt());
        return response;
    }
}
