package org.homedecoration.stage.review.service;

import lombok.RequiredArgsConstructor;
import org.homedecoration.house.entity.House;
import org.homedecoration.house.repository.HouseRepository;
import org.homedecoration.identity.worker.entity.Worker;
import org.homedecoration.identity.worker.repository.WorkerRepository;
import org.homedecoration.stage.assignment.entity.StageAssignment;
import org.homedecoration.stage.assignment.repository.StageAssignmentRepository;
import org.homedecoration.stage.review.dto.request.CreateStageReviewRequest;
import org.homedecoration.stage.review.entity.StageReview;
import org.homedecoration.stage.review.repository.StageReviewRepository;
import org.homedecoration.stage.stage.entity.Stage;
import org.homedecoration.stage.stage.repository.StageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StageReviewService {

    private final StageRepository stageRepository;
    private final HouseRepository houseRepository;
    private final StageAssignmentRepository stageAssignmentRepository;
    private final StageReviewRepository stageReviewRepository;
    private final WorkerRepository workerRepository;

    /**
     * 评价单个 assignment
     */
    @Transactional
    public StageReview createReviewForAssignment(Long assignmentId, Long reviewerId,
                                                 double rating, String comment) {

        StageAssignment assignment = stageAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("派工记录不存在"));

        Stage stage = stageRepository.findById(assignment.getStageId())
                .orElseThrow(() -> new RuntimeException("阶段不存在"));

        House house = houseRepository.findById(stage.getHouseId())
                .orElseThrow(() -> new RuntimeException("房屋不存在"));

        // 权限校验
        if (!house.getUser().getId().equals(reviewerId)) {
            throw new RuntimeException("无权限评价该阶段");
        }

        // 防止重复评价
        if (stageReviewRepository.existsByAssignmentIdAndReviewerId(assignmentId, reviewerId)) {
            throw new RuntimeException("已评价，不能重复提交");
        }

        // 计算有效天数
        LocalDate start = assignment.getStartAt().toLocalDate();
        LocalDate end = assignment.getEndAt().toLocalDate();
        int effectiveDays = (int) ChronoUnit.DAYS.between(start, end) + 1;
        effectiveDays = Math.max(1, effectiveDays);

        // 创建评价
        StageReview review = new StageReview();
        review.setAssignmentId(assignmentId);
        review.setStageId(stage.getId());
        review.setReviewerId(reviewerId);
        review.setRating(rating);
        review.setComment(comment);
        stageReviewRepository.save(review);

        // 更新工人评分
        Worker worker = assignment.getWorker();
        // 更新工人评分，使用 BigDecimal 精确计算
        BigDecimal oldTotal = worker.getRating()
                .multiply(BigDecimal.valueOf(worker.getTotalWorkDays()));
        BigDecimal newScore = BigDecimal.valueOf(rating)
                .multiply(BigDecimal.valueOf(effectiveDays));

        BigDecimal totalScore = oldTotal.add(newScore);
        int newTotalDays = worker.getTotalWorkDays() + effectiveDays;

        BigDecimal newRating = totalScore.divide(BigDecimal.valueOf(newTotalDays), 2, RoundingMode.HALF_UP);

        worker.setRating(newRating);
        worker.setTotalWorkDays(newTotalDays);
        workerRepository.save(worker);
        return review;
    }

    /**
     * 批量评价阶段（适合原来的接口）
     */
    @Transactional
    public List<StageReview> createStageReview(Long stageId, CreateStageReviewRequest request, Long reviewerId) {
        List<StageAssignment> assignments = stageAssignmentRepository.findByStageId(stageId);
        if (assignments.isEmpty()) {
            throw new RuntimeException("当前阶段没有派工记录，无法评价");
        }

        List<StageReview> stageReviews = new ArrayList<>();
        for (StageAssignment assignment : assignments) {
            StageReview review = createReviewForAssignment(
                    assignment.getId(),
                    reviewerId,
                    request.getRating(),
                    request.getComment()
            );
            stageReviews.add(review);
        }
        return stageReviews;
    }

    public List<StageReview> listByStageId(Long stageId) {
        return stageReviewRepository.findByStageIdOrderByCreatedAtDesc(stageId);
    }
}

