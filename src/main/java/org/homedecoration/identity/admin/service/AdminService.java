package org.homedecoration.identity.admin.service;

import jakarta.transaction.Transactional;
import org.homedecoration.identity.designer.entity.Designer;
import org.homedecoration.identity.designer.repository.DesignerRepository;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.identity.user.repository.UserRepository;
import org.homedecoration.identity.admin.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final DesignerRepository designerRepository;

    public AdminService(UserRepository userRepository,
                        AdminRepository adminRepository,
                        DesignerRepository designerRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.designerRepository = designerRepository;
    }


    private void checkAdmin(Long adminId) {
        if (!adminRepository.existsById(adminId)) {
            throw new RuntimeException("当前用户不是管理员，无法操作");
        }
    }

    @Transactional
    public User updateStatus(Long targetUserId, User.Status status, Long adminId) {
        checkAdmin(adminId);

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setStatus(status);

        return userRepository.save(user);
    }

    @Transactional
    public User updateRole(Long targetUserId, User.Role role, Long adminId) {
        checkAdmin(adminId);

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setRole(role);

        return userRepository.save(user);
    }


    public List<Designer> getPendingDesigners(Long adminId) {
        checkAdmin(adminId);
        return designerRepository.findByVerifyStatus(Designer.VerifyStatus.PENDING);
    }

    @Transactional
    public Designer updateVerifyStatus(Long designerId, Designer.VerifyStatus status, Long adminId) {
        checkAdmin(adminId);

        Designer designer = designerRepository.findById(designerId)
                .orElseThrow(() -> new RuntimeException("设计师不存在"));

        designer.setVerifyStatus(status);
        if(status == Designer.VerifyStatus.APPROVED) designer.setEnabled(true);

        return designerRepository.save(designer);
    }

}
