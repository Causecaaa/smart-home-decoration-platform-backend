package org.homedecoration.service;

import org.homedecoration.entity.*;
import org.homedecoration.dto.request.CreateLayoutImageRequest;
import org.homedecoration.repository.HouseLayoutImageRepository;
import org.homedecoration.repository.HouseLayoutRepository;
import org.homedecoration.utils.LayoutPermissionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class HouseLayoutImageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final HouseLayoutRepository layoutRepository;
    private final UserService userService;
    private final LayoutPermissionUtil layoutPermissionUtil;
    private final HouseLayoutImageRepository houseLayoutImageRepository;

    public HouseLayoutImageService(HouseLayoutRepository layoutRepository,
                                   UserService userService,
                                   LayoutPermissionUtil layoutPermissionUtil,
                                   HouseLayoutImageRepository houseLayoutImageRepository) {
        this.layoutRepository = layoutRepository;
        this.userService = userService;
        this.layoutPermissionUtil = layoutPermissionUtil;
        this.houseLayoutImageRepository = houseLayoutImageRepository;
    }


    public HouseLayoutImage getImageById(Long imageId) {
        return houseLayoutImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
    }

    @Transactional
    public HouseLayoutImage createImage(Long layoutId, CreateLayoutImageRequest request, Long userId) {
        // 1️⃣ 查找布局
        HouseLayout layout = layoutRepository.findById(layoutId)
                .orElseThrow(() -> new RuntimeException("Layout not found"));

        // 2️⃣ 查找操作用户
        User operator = userService.getById(userId);

        // 3️⃣ 权限校验
        System.out.println("===== canEdit check =====");
        System.out.println("operator_role = " + operator.getRole());
        System.out.println("house_id = " + layout.getHouse().getId());
        System.out.println("user_id = " + userId);

        layoutPermissionUtil.checkCanEdit(operator, layout, userId);



        // 4️⃣ 确定图片类型
        HouseLayoutImage.ImageType type = request.getImageType();
        switch (operator.getRole()) {
            case USER -> {
                if (type == null) type = HouseLayoutImage.ImageType.ORIGINAL;
                else if (type != HouseLayoutImage.ImageType.ORIGINAL && type != HouseLayoutImage.ImageType.USER) {
                    throw new RuntimeException("Users can only upload ORIGINAL or USER images");
                }
            }
            case DESIGNER -> {
                if (type == null) type = HouseLayoutImage.ImageType.STRUCTURE;
                else if (type != HouseLayoutImage.ImageType.STRUCTURE && type != HouseLayoutImage.ImageType.FURNITURE) {
                    throw new RuntimeException("Designers can only upload STRUCTURE or FURNITURE images");
                }
            }
            default -> throw new RuntimeException("Unknown role");
        }

        // 5️⃣ 保存文件到本地
        String filename = null;
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            try {
                String originalName = request.getFile().getOriginalFilename();
                filename = System.currentTimeMillis() + "_" + originalName;
                Path path = Paths.get(uploadDir, filename);
                Files.createDirectories(path.getParent());
                request.getFile().transferTo(path.toFile());
            } catch (Exception e) {
                throw new RuntimeException("Failed to save file: " + e.getMessage(), e);
            }
        }

        // 6️⃣ 构建实体并保存
        HouseLayoutImage image = new HouseLayoutImage();
        image.setLayout(layout);
        image.setImageDesc(request.getImageDesc());
        image.setImageType(type);
        // 如果上传了文件，就保存本地路径，否则使用前端传的 imageUrl
        image.setImageUrl(filename != null ? "/uploads/" + filename : request.getImageUrl());

        return houseLayoutImageRepository.save(image);
    }
}
