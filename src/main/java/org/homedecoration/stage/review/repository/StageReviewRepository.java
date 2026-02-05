package org.homedecoration.stage.review.repository;

import org.homedecoration.stage.review.entity.StageReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StageReviewRepository extends JpaRepository<StageReview, Long> {
    boolean existsByAssignmentIdAndReviewerId(Long assignmentId, Long reviewerId);

    List<StageReview> findByStageIdOrderByCreatedAtDesc(Long stageId);
}
