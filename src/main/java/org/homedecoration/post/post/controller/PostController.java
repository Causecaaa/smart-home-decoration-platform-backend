package org.homedecoration.post.post.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.post.post.dto.request.PostRequest;
import org.homedecoration.post.post.dto.response.DetailPostResponse;
import org.homedecoration.post.post.dto.response.SimplePostResponse;
import org.homedecoration.post.post.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;

    /**
     * 创建文章（返回 Simple）
     */
    @PostMapping("/create")
    public ApiResponse<SimplePostResponse> createPost(
            @RequestBody @Valid PostRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(
                postService.createPost(request, userId)
        );
    }

    /**
     * 首页文章列表（Simple）
     */
    @GetMapping("/list")
    public ApiResponse<List<SimplePostResponse>> list() {
        return ApiResponse.success(
                postService.listSimplePosts()
        );
    }

    /**
     * 文章详情（Simple）
     */
    @GetMapping("/{postId}/simple")
    public ApiResponse<SimplePostResponse> simple(
            @PathVariable Long postId,
            HttpServletRequest httpRequest
    ) {
        Long userId = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(
                postService.getSimple(postId, userId)
        );
    }

    /**
     * 文章详情（Detail）
     */
    @GetMapping("/{postId}")
    public ApiResponse<DetailPostResponse> detail(
            @PathVariable Long postId,
            HttpServletRequest httpRequest
    ) {
        Long userId = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(
                postService.getDetail(postId, userId)
        );
    }

    /**
     * 修改文章（Detail）
     */
    @PutMapping("/{postId}")
    public ApiResponse<DetailPostResponse> update(
            @PathVariable Long postId,
            @RequestBody @Valid PostRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(
                postService.updatePost(postId, request, userId)
        );
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> delete(
            @PathVariable Long postId,
            HttpServletRequest httpRequest
    ) {
        Long userId = jwtUtil.getUserId(httpRequest);
        postService.deletePost(postId, userId);
        return ApiResponse.success(null);
    }
}
