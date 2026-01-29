package org.homedecoration.post.comment.dto.response;

import lombok.Data;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.post.comment.entity.PostComment;

import java.time.LocalDateTime;

@Data
public class CommentResponse {

    private Long commentId;
    private String content;
    private LocalDateTime createdAt;

    /* 作者 */
    private Long authorId;
    private String authorName;
    private String authorAvatar;

    public static CommentResponse toDTO(PostComment comment, User author) {
        CommentResponse response = new CommentResponse();
        response.setCommentId(comment.getId());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());

        response.setAuthorId(author.getId());
        response.setAuthorName(author.getUsername());
        response.setAuthorAvatar(author.getAvatarUrl());

        return response;
    }
}

