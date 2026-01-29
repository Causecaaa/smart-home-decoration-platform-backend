package org.homedecoration.post.comment.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.post.comment.dto.request.CommentRequest;
import org.homedecoration.post.comment.dto.response.CommentResponse;
import org.homedecoration.post.comment.service.PostCommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post/{postId}/comment")
public class PostCommentController {

    private final PostCommentService commentService;
    private final JwtUtil jwtUtil;

    /**
     * 发表评论
     */
    @PostMapping
    public ApiResponse<CommentResponse> create(
            @PathVariable Long postId,
            @RequestBody @Valid CommentRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(
                commentService.createComment(postId, request, userId)
        );
    }

    /**
     * 评论列表
     */
    @GetMapping
    public ApiResponse<List<CommentResponse>> list(
            @PathVariable Long postId
    ) {
        return ApiResponse.success(
                commentService.listComments(postId)
        );
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> delete(
            @PathVariable Long postId,   // 这里暂时不用，但路径语义正确
            @PathVariable Long commentId,
            HttpServletRequest httpRequest
    ) {
        Long userId = jwtUtil.getUserId(httpRequest);
        commentService.deleteComment(commentId, userId);
        return ApiResponse.success(null);
    }

}
