package org.homedecoration.identity.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.identity.admin.service.AdminService;
import org.homedecoration.identity.designer.entity.Designer;
import org.homedecoration.identity.user.dto.response.UserResponse;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.identity.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;

    public AdminController(AdminService adminService, JwtUtil jwtUtil) {
        this.adminService = adminService;
        this.jwtUtil = jwtUtil;
    }

    @PatchMapping("/{id}/update-status")
    public ApiResponse<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestParam User.Status status,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.success(
                UserResponse.toDTO(adminService.updateStatus(id, status ,jwtUtil.getUserId(httpRequest)))
        );
    }

    @PatchMapping("/{id}/update-role")
    public ApiResponse<UserResponse> updateUserRole(
            @PathVariable Long id,
            @RequestParam User.Role role,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.success(
                UserResponse.toDTO(adminService.updateRole(id, role ,jwtUtil.getUserId(httpRequest)))
        );
    }

    @GetMapping("/designer/pending")
    public ApiResponse<List<Designer>> getPendingDesigners(HttpServletRequest request) {
        Long adminId = jwtUtil.getUserId(request);
        return ApiResponse.success(
                adminService.getPendingDesigners(adminId)
        );
    }

    @PatchMapping("/designer/{id}/verify-status")
    public ApiResponse<Designer> updateVerifyStatus(
            @PathVariable Long id,
            @RequestParam Designer.VerifyStatus status,
            HttpServletRequest request
    ) {
        Designer designer = adminService.updateVerifyStatus(id, status, jwtUtil.getUserId(request));
        return ApiResponse.success(designer);
    }
}
