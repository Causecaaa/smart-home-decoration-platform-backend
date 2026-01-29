package org.homedecoration.post.comment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.homedecoration.identity.user.service.UserService;
import org.homedecoration.post.comment.dto.request.CommentRequest;
import org.homedecoration.post.comment.dto.response.CommentResponse;
import org.homedecoration.post.comment.entity.PostComment;
import org.homedecoration.post.comment.repository.PostCommentRepository;
import org.homedecoration.post.post.entity.Post;
import org.homedecoration.post.post.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommentService {

    private final PostCommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    @Transactional
    public CommentResponse createComment(Long postId, CommentRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        PostComment comment = new PostComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        PostComment saved = commentRepository.save(comment);

        // 评论数 +1
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        return CommentResponse.toDTO(saved, userService.getById(userId));
    }

    public List<CommentResponse> listComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(c -> CommentResponse.toDTO(
                        c,
                        userService.getById(c.getUserId())
                ))
                .toList();
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {

        PostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));

        // 只能删自己的评论
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该评论");
        }

        Post post = postRepository.findById(comment.getPostId())
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        commentRepository.delete(comment);

        // 评论数 -1（兜底，防止负数）
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
        postRepository.save(post);
    }

}

