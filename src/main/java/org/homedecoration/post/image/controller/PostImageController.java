package org.homedecoration.post.image.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.post.image.dto.response.PostImageResponse;
import org.homedecoration.post.image.service.PostImageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post/image")
public class PostImageController {

    private final PostImageService imageService;
    private final JwtUtil jwtUtil;

    /**
     * 上传图片
     */
    @PostMapping("/{postId}")
    public ApiResponse<PostImageResponse> upload(
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) {
        Long userId = jwtUtil.getUserId(request);
        return ApiResponse.success(
                imageService.uploadImage(postId, file, userId)
        );
    }

    /**
     * 删除图片
     */
    @DeleteMapping("/{imageId}")
    public ApiResponse<Void> delete(
            @PathVariable Long imageId,
            HttpServletRequest request
    ) {
        Long userId = jwtUtil.getUserId(request);
        imageService.deleteImage(imageId, userId);
        return ApiResponse.success(null);
    }
}
