//package org.homedecoration.furnitureImage.service;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.homedecoration.furnitureImage.dto.request.CreateFurnitureImageRequest;
//import org.homedecoration.furnitureImage.entity.FurnitureImage;
//import org.homedecoration.furnitureImage.repository.FurnitureImageRepository;
//import org.homedecoration.furnitureScheme.entity.FurnitureScheme;
//import org.homedecoration.furnitureScheme.service.FurnitureSchemeService;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//
//@Service
//@RequiredArgsConstructor
//public class FurnitureImageService {
//
//    private final FurnitureImageRepository furnitureImageRepository;
//    private final FurnitureSchemeService schemeService;
//    private final FileStorageService fileStorageService;
//
//    @Transactional
//    public FurnitureImage createImage(
//            Long schemeId,
//            CreateFurnitureImageRequest request,
//            Long userId
//    ) throws IOException {
//
//        FurnitureScheme scheme = schemeService.getById(schemeId);
//
//        // 1️⃣ 权限校验（设计师 or 房屋用户，按你项目规则）
//        schemeService.checkImageOperatePermission(scheme, userId);
//
//        // 2️⃣ imageType 默认值
//        FurnitureImage.ImageType imageType =
//                request.getImageType() != null
//                        ? request.getImageType()
//                        : FurnitureImage.ImageType.DESIGN;
//
//        // 3️⃣ CONFIRMED 图片不允许手动上传（强烈建议）
//        if (imageType == FurnitureImage.ImageType.CONFIRMED) {
//            throw new IllegalStateException("CONFIRMED 图片只能由确认流程生成");
//        }
//
//        // 4️⃣ file / imageUrl 二选一校验
//        if (request.getFile() == null && request.getImageUrl() == null) {
//            throw new IllegalArgumentException("必须上传文件或提供 imageUrl");
//        }
//
//        String finalImageUrl;
//
//        if (request.getFile() != null) {
//            // 上传文件
//            finalImageUrl = fileStorageService.storeSchemeImage(
//                    schemeId,
//                    request.getFile()
//            );
//        } else {
//            // 前端直接给 URL
//            finalImageUrl = request.getImageUrl();
//        }
//
//        // 5️⃣ 保存实体
//        FurnitureImage image = new FurnitureImage();
//        image.setScheme(scheme);
//        image.setImageType(imageType);
//        image.setImageUrl(finalImageUrl);
//
//        return furnitureImageRepository.save(image);
//    }
//}
