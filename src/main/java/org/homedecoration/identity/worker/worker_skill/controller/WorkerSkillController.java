package org.homedecoration.identity.worker.worker_skill.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.identity.worker.worker_skill.dto.request.CreateWorkerSkillRequest;
import org.homedecoration.identity.worker.worker_skill.dto.request.UpdateWorkerSkillRequest;
import org.homedecoration.identity.worker.worker_skill.dto.response.WorkerSkillResponse;
import org.homedecoration.identity.worker.worker_skill.repository.WorkerSkill;
import org.homedecoration.identity.worker.worker_skill.service.WorkerSkillService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/worker-skill")
@RequiredArgsConstructor
public class WorkerSkillController {

    private final WorkerSkillService workerSkillService;
    private final JwtUtil jwtUtil;

    @PostMapping("/create")
    public ApiResponse<WorkerSkillResponse> createSkill(
            HttpServletRequest request,
            @RequestBody CreateWorkerSkillRequest body) {
        Long workerId = jwtUtil.getUserId(request);
        WorkerSkill skill = workerSkillService.createSkill(workerId, body);
        return ApiResponse.success(WorkerSkillResponse.toDTO(skill));
    }

    @GetMapping("/my/list")
    public ApiResponse<List<WorkerSkillResponse>> listMySkills(HttpServletRequest request) {
        Long workerId = jwtUtil.getUserId(request);
        return ApiResponse.success(toResponseList(workerSkillService.listByWorkerId(workerId)));
    }

    @GetMapping("/{workerId}/list")
    public ApiResponse<List<WorkerSkillResponse>> listWorkerSkills(@PathVariable Long workerId) {
        return ApiResponse.success(toResponseList(workerSkillService.listByWorkerId(workerId)));
    }

    @PutMapping("/{skillId}/update")
    public ApiResponse<WorkerSkillResponse> updateSkill(
            @PathVariable Long skillId,
            @RequestBody UpdateWorkerSkillRequest body) {
        WorkerSkill skill = workerSkillService.updateSkill(skillId, body);
        return ApiResponse.success(WorkerSkillResponse.toDTO(skill));
    }

    @DeleteMapping("/{skillId}")
    public ApiResponse<Void> deleteSkill(@PathVariable Long skillId) {
        workerSkillService.deleteSkill(skillId);
        return ApiResponse.success(null);
    }

    private List<WorkerSkillResponse> toResponseList(List<WorkerSkill> skills) {
        return skills.stream()
                .map(WorkerSkillResponse::toDTO)
                .collect(Collectors.toList());
    }
}
