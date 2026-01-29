package org.homedecoration.post.like.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.post.like.service.PostLikeService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostLikeController {

    private final PostLikeService likeService;
    private final JwtUtil jwtUtil;

    @PostMapping("/{postId}/like")
    public ApiResponse<Boolean> toggleLike(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        Long userId = jwtUtil.getUserId(request);
        boolean liked = likeService.toggleLike(postId, userId);
        return ApiResponse.success(liked);
    }
}
