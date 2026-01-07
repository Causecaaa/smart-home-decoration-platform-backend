package org.homedecoration.identity.designer.service;

import jakarta.validation.Valid;
import org.homedecoration.common.exception.BusinessException;
import org.homedecoration.identity.designer.dto.request.CreateDesignerRequest;
import org.homedecoration.identity.designer.dto.request.UpdateDesignerProfileRequest;
import org.homedecoration.identity.designer.entity.Designer;
import org.homedecoration.identity.designer.repository.DesignerRepository;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.identity.user.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class DesignerService {

    private final DesignerRepository designerRepository;
    private final UserService userService;

    public DesignerService(
            DesignerRepository designerRepository,
            UserService userService) {
        this.designerRepository = designerRepository;
        this.userService = userService;
    }

    /**
     * 申请成为设计师
     */
    public Designer apply(Long userId, @Valid CreateDesignerRequest request) {

        // 已经是设计师，不能重复申请
        if (designerRepository.existsById(userId)) {
            throw new IllegalStateException("用户已是设计师");
        }

        // 获取用户
        User user = userService.getById(userId);

        // 创建设计师实体
        Designer designer = new Designer();
        designer.setUser(user); // MapsId 会自动同步 userId

        designer.setRealName(request.getRealName());
        designer.setExperienceYears(request.getExperienceYears());
        designer.setStyle(request.getStyle());
        designer.setBio(request.getBio());

        // 平台控制字段
        designer.setVerifyStatus(Designer.VerifyStatus.PENDING);
        designer.setEnabled(false);
        designer.setRating(0.0);
        designer.setOrderCount(0);

        // 更新用户角色
        userService.updateRole(userId, User.Role.DESIGNER);

        return designerRepository.save(designer);
    }

    /**
     * 根据 userId 查询设计师
     */
    public Designer getByUserId(Long userId) {
        return designerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("设计师不存在"));
    }

    @Transactional
    public Designer updateProfile(Long userId, @Valid UpdateDesignerProfileRequest body) {

        Designer designer = getByUserId(userId);

        // ===== 只更新允许修改的字段 =====
        if (body.getRealName() != null) {
            designer.setRealName(body.getRealName());
        }

        if (body.getExperienceYears() != null) {
            designer.setExperienceYears(body.getExperienceYears());
        }

        if (body.getStyle() != null) {
            designer.setStyle(body.getStyle());
        }

        if (body.getBio() != null) {
            designer.setBio(body.getBio());
        }


        return designerRepository.save(designer);
    }

    public List<Designer> list(String keyword, String sortBy, String order) {
        // 允许排序字段白名单
        Set<String> allowedSortFields = Set.of("rating", "experienceYears", "createdAt");
        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "rating"; // 默认按评分
        }

        Sort sort = order.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        if (keyword == null || keyword.isBlank()) {
            return designerRepository.findByEnabledTrue(sort);
        } else {
            // 模糊匹配 realName 或 style
            return designerRepository.findByEnabledTrueAndKeyword(keyword, sort);
        }
    }
}
