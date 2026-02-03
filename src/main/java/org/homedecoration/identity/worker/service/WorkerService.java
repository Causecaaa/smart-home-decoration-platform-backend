package org.homedecoration.identity.worker.service;

import jakarta.validation.Valid;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.identity.user.service.UserService;
import org.homedecoration.identity.worker.dto.request.CreateWorkerRequest;
import org.homedecoration.identity.worker.dto.request.UpdateWorkerProfileRequest;
import org.homedecoration.identity.worker.dto.response.WorkerDetailResponse;
import org.homedecoration.identity.worker.entity.Worker;
import org.homedecoration.identity.worker.repository.WorkerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final UserService userService;

    public WorkerService(WorkerRepository workerRepository, UserService userService) {
        this.workerRepository = workerRepository;
        this.userService = userService;
    }

    public WorkerDetailResponse apply(Long userId, @Valid CreateWorkerRequest request) {
        if (workerRepository.existsById(userId)) {
            throw new IllegalStateException("用户已是工人");
        }

        User user = userService.getById(userId);

        Worker worker = new Worker();
        worker.setUserId(userId);
        worker.setRealName(request.getRealName());
        worker.setCity(request.getCity());
        worker.setIsPlatformWorker(request.getIsPlatformWorker());
        worker.setWorkStatus(
                request.getWorkStatus() == null
                        ? Worker.WorkStatus.IDLE
                        : request.getWorkStatus()
        );

        workerRepository.save(worker);

        userService.updateRole(userId, User.Role.WORKER);

        return WorkerDetailResponse.toDTO(worker, user);
    }

    public WorkerDetailResponse getDetailById(Long workerId) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("工人不存在"));
        User user = userService.getById(workerId);
        return WorkerDetailResponse.toDTO(worker, user);
    }

    public WorkerDetailResponse updateProfile(Long userId, @Valid UpdateWorkerProfileRequest body) {
        Worker worker = workerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("工人不存在"));

        if (body.getRealName() != null) {
            worker.setRealName(body.getRealName());
        }
        if (body.getCity() != null) {
            worker.setCity(body.getCity());
        }
        if (body.getIsPlatformWorker() != null) {
            worker.setIsPlatformWorker(body.getIsPlatformWorker());
        }
        if (body.getWorkStatus() != null) {
            worker.setWorkStatus(body.getWorkStatus());
        }
        if (body.getEnabled() != null) {
            worker.setEnabled(body.getEnabled());
        }

        workerRepository.save(worker);

        User user = userService.getById(userId);
        return WorkerDetailResponse.toDTO(worker, user);
    }

    public List<WorkerDetailResponse> list(String city, Worker.WorkStatus workStatus, Boolean enabled) {
        List<Worker> workers;
        if (city != null && workStatus != null && enabled != null) {
            workers = workerRepository.findByCityAndWorkStatusAndEnabled(city, workStatus, enabled);
        } else if (city != null) {
            workers = workerRepository.findByCity(city);
        } else {
            workers = workerRepository.findAll();
        }

        return workers.stream()
                .map(worker -> WorkerDetailResponse.toDTO(worker, userService.getById(worker.getUserId())))
                .toList();
    }
}
