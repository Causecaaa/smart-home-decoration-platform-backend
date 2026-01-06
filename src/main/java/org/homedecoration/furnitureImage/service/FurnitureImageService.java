package org.homedecoration.furnitureImage.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.homedecoration.furnitureImage.entity.FurnitureImage;
import org.homedecoration.furnitureImage.repository.FurnitureImageRepository;
import org.homedecoration.furnitureScheme.entity.FurnitureScheme;
import org.homedecoration.furnitureScheme.service.FurnitureSchemeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FurnitureImageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final FurnitureImageRepository furnitureImageRepository;
    private final FurnitureSchemeService schemeService;

    @Transactional
    public FurnitureImage createImage(Long schemeId, MultipartFile file, Long designerId) throws IOException {

        FurnitureScheme scheme = schemeService.getById(schemeId);

        check(scheme, designerId);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("必须上传文件");
        }

        String originalName = file.getOriginalFilename();
        String filename = System.currentTimeMillis() + "_" + originalName;
        Path path = Paths.get(uploadDir + "/furniture", filename);
        Files.createDirectories(path.getParent());
        file.transferTo(path.toFile());

        FurnitureImage image = new FurnitureImage();
        image.setScheme(scheme);
        image.setImageUrl("/uploads/furniture/" + filename);

        return furnitureImageRepository.save(image);
    }

    @Transactional
    public void deleteImage(Long schemeId, Long designerId) {
        // 获取方案
        FurnitureScheme scheme = schemeService.getById(schemeId);

        // 权限校验
        check(scheme, designerId);

        // 获取图片
        FurnitureImage image = furnitureImageRepository.findByScheme(scheme)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "方案没有图片"));

        // 删除文件
        try {
            Path path = Paths.get(uploadDir + image.getImageUrl().replace("/uploads", ""));
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("删除图片文件失败: " + e.getMessage(), e);
        }

        // 删除数据库记录
        furnitureImageRepository.delete(image);
    }

    private void check(FurnitureScheme scheme, Long designerId) {
        if (!scheme.getDesignerId().equals(designerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "designerId不匹配");
        }

        // 状态校验：必须为 SUBMITTED
        if (scheme.getSchemeStatus() != FurnitureScheme.SchemeStatus.SUBMITTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "方案状态必须为 SUBMITTED 才能删除图片");
        }
    }
}
