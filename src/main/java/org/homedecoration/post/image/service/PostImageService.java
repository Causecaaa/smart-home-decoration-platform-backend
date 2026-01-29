package org.homedecoration.post.image.service;

import lombok.RequiredArgsConstructor;
import org.homedecoration.post.image.dto.response.PostImageResponse;
import org.homedecoration.post.image.entity.PostImage;
import org.homedecoration.post.image.repository.PostImageRepository;
import org.homedecoration.post.post.entity.Post;
import org.homedecoration.post.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostImageService {

    private final PostImageRepository imageRepository;
    private final PostRepository postRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 上传文章图片
     */
    @Transactional
    public PostImageResponse uploadImage(Long postId, MultipartFile file, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("无权给该文章上传图片");
        }

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        String filename;
        try {
            String originalName = file.getOriginalFilename();
            filename = System.currentTimeMillis() + "_" + originalName;

            // uploadDir/post/xxx.jpg
            Path path = Paths.get(uploadDir, "post", filename);
            Files.createDirectories(path.getParent());
            file.transferTo(path.toFile());

        } catch (Exception e) {
            throw new RuntimeException("图片保存失败：" + e.getMessage(), e);
        }

        PostImage image = new PostImage();
        image.setPostId(postId);
        image.setImageUrl("/uploads/post/" + filename);
        image.setCreatedAt(LocalDateTime.now());

        PostImage saved = imageRepository.save(image);
        return PostImageResponse.toDTO(saved);
    }

    /**
     * 删除图片
     */
    @Transactional
    public void deleteImage(Long imageId, Long userId) {

        PostImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("图片不存在"));

        Post post = postRepository.findById(image.getPostId())
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该图片");
        }

        // 可选：物理删除文件（现在可以先不做）
        imageRepository.delete(image);
    }


}
