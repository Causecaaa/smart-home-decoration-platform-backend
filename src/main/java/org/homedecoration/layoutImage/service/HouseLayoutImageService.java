package org.homedecoration.layoutImage.service;

import org.homedecoration.common.utils.LayoutPermissionUtil;
import org.homedecoration.house.entity.House;
import org.homedecoration.house.repository.HouseRepository;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.identity.user.service.UserService;
import org.homedecoration.layout.entity.HouseLayout;
import org.homedecoration.layout.repository.HouseLayoutRepository;
import org.homedecoration.layout.service.HouseLayoutService;
import org.homedecoration.layoutImage.dto.request.CreateLayoutImageRequest;
import org.homedecoration.layoutImage.entity.HouseLayoutImage;
import org.homedecoration.layoutImage.repository.HouseLayoutImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class HouseLayoutImageService {
    private final HouseLayoutService houseLayoutService;
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final HouseLayoutRepository layoutRepository;
    private final UserService userService;
    private final LayoutPermissionUtil layoutPermissionUtil;
    private final HouseLayoutImageRepository houseLayoutImageRepository;
    private final HouseRepository houseRepository;

    public HouseLayoutImageService(HouseLayoutRepository layoutRepository,
                                   UserService userService,
                                   LayoutPermissionUtil layoutPermissionUtil,
                                   HouseLayoutImageRepository houseLayoutImageRepository,
                                   HouseLayoutService houseLayoutService,
                                   HouseRepository houseRepository) {
        this.layoutRepository = layoutRepository;
        this.userService = userService;
        this.layoutPermissionUtil = layoutPermissionUtil;
        this.houseLayoutImageRepository = houseLayoutImageRepository;
        this.houseLayoutService = houseLayoutService;
        this.houseRepository = houseRepository;
    }


    public HouseLayoutImage getImageById(Long imageId) {
        return houseLayoutImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
    }

    public List<HouseLayoutImage> getImagesByLayoutId(Long layoutId) {
        return houseLayoutImageRepository.findByLayout_Id(layoutId);
    }

    @Transactional
    public HouseLayoutImage createImage(Long layoutId, CreateLayoutImageRequest request, Long userId) {
        HouseLayout layout = houseLayoutService.getLayoutById(layoutId);
        User operator = userService.getById(userId);

        layoutPermissionUtil.checkCanEdit(operator, layout, userId);


        HouseLayoutImage.ImageType type = request.getImageType();
        switch (operator.getRole()) {
            case USER -> {
                if (type == null) type = HouseLayoutImage.ImageType.ORIGINAL;
                else if (type != HouseLayoutImage.ImageType.ORIGINAL && type != HouseLayoutImage.ImageType.STRUCTURE) {
                    throw new RuntimeException("Users can only upload ORIGINAL or STRUCTURE images");
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

        String filename = null;
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            try {
                String originalName = request.getFile().getOriginalFilename();
                filename = System.currentTimeMillis() + "_" + originalName;
                Path path = Paths.get(uploadDir, "layout", filename);
                Files.createDirectories(path.getParent());
                request.getFile().transferTo(path.toFile());
            } catch (Exception e) {
                throw new RuntimeException("Failed to save file: " + e.getMessage(), e);
            }
        }

        HouseLayoutImage image = new HouseLayoutImage();
        image.setLayout(layout);
        image.setImageDesc(request.getImageDesc());
        image.setImageType(type);

        image.setImageUrl("/uploads/layout/" + filename);

        return houseLayoutImageRepository.save(image);
    }


    @Transactional
    public void deleteImage(Long imageId, Long userId) {
        HouseLayoutImage image = getImageById(imageId);
        User operator = userService.getById(userId);

        layoutPermissionUtil.checkCanEdit(operator, image.getLayout(), userId);

        houseLayoutImageRepository.delete(image);
    }

    @Transactional
    public HouseLayoutImage updateImage(Long imageId, CreateLayoutImageRequest request, Long userId) {
        HouseLayoutImage image = getImageById(imageId);
        User operator = userService.getById(userId);

        layoutPermissionUtil.checkCanEdit(operator, image.getLayout(), userId);

        image.setImageDesc(request.getImageDesc());

        return houseLayoutImageRepository.save(image);
    }
}
