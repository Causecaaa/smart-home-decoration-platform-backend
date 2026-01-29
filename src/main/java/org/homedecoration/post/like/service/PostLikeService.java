package org.homedecoration.post.like.service;

import lombok.RequiredArgsConstructor;
import org.homedecoration.post.like.entity.PostLike;
import org.homedecoration.post.like.repository.PostLikeRepository;
import org.homedecoration.post.post.entity.Post;
import org.homedecoration.post.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository likeRepository;
    private final PostRepository postRepository;

    @Transactional
    public boolean toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        return likeRepository.findByPostIdAndUserId(postId, userId)
                .map(like -> {
                    // 已点赞 → 取消
                    likeRepository.delete(like);
                    post.setLikeCount(post.getLikeCount() - 1);
                    postRepository.save(post);
                    return false;
                })
                .orElseGet(() -> {
                    // 未点赞 → 点赞
                    PostLike like = new PostLike();
                    like.setPostId(postId);
                    like.setUserId(userId);
                    like.setCreatedAt(LocalDateTime.now());
                    likeRepository.save(like);

                    post.setLikeCount(post.getLikeCount() + 1);
                    postRepository.save(post);
                    return true;
                });
    }

    public boolean hasLiked(Long postId, Long userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }
}
