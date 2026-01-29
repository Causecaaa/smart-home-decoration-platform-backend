package org.homedecoration.post.post.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.identity.user.service.UserService;
import org.homedecoration.post.image.entity.PostImage;
import org.homedecoration.post.image.repository.PostImageRepository;
import org.homedecoration.post.image.service.PostImageService;
import org.homedecoration.post.like.repository.PostLikeRepository;
import org.homedecoration.post.post.dto.request.PostRequest;
import org.homedecoration.post.post.dto.response.DetailPostResponse;
import org.homedecoration.post.post.dto.response.SimplePostResponse;
import org.homedecoration.post.post.entity.Post;
import org.homedecoration.post.post.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final PostImageRepository imageRepository;
    private final PostLikeRepository postLikeRepository;

    /**
     * 创建文章
     */
    public SimplePostResponse createPost(PostRequest request, Long userId) {
        User user = userService.getById(userId);

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUserId(userId);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setLikeCount(0);
        post.setCommentCount(0);

        Post saved = postRepository.save(post);
        return SimplePostResponse.toDTO(saved, user, null);
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
    }

    public void deletePost(Long postId, Long userId) {
        Post post = getPostById(postId);
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该文章");
        }
        postRepository.delete(post);
    }

    public List<SimplePostResponse> listSimplePosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(post -> {
                    User author = userService.getById(post.getUserId());
                    List<PostImage> images =
                            imageRepository.findTop3ByPostIdOrderByCreatedAtAsc(post.getId());
                    return SimplePostResponse.toDTO(post, author, images);
                })
                .toList();
    }

    public DetailPostResponse getDetail(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        User author = userService.getById(post.getUserId());

        List<PostImage> images =
                imageRepository.findByPostIdOrderByCreatedAtAsc(postId);

        boolean liked = postLikeRepository.existsByPostIdAndUserId(postId, userId);

        return DetailPostResponse.toDTO(post, author, liked, images);
    }

    public DetailPostResponse updatePost(Long postId,
                                         @Valid PostRequest request,
                                         Long userId) {
        Post post = getPostById(postId);
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改该文章");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        Post saved = postRepository.save(post);

        User author = userService.getById(userId);

        List<PostImage> images =
                imageRepository.findByPostIdOrderByCreatedAtAsc(postId);

        boolean liked = false;

        return DetailPostResponse.toDTO(saved, author, liked, images);
    }

}

