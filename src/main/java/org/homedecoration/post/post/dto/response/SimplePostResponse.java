package org.homedecoration.post.post.dto.response;

import lombok.Data;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.identity.user.service.UserService;
import org.homedecoration.post.image.dto.response.PostImageResponse;
import org.homedecoration.post.image.entity.PostImage;
import org.homedecoration.post.post.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SimplePostResponse {
    private Long authorId;
    private String authorName;
    private String authorAvatar;

    private Long postId;
    private String title;
    private String summary;
    private int likeCount;
    private int commentCount;

    /* ========= 扩展 ========= */
    private List<PostImageResponse> previewImages;

    public static SimplePostResponse toDTO(Post post, User author, List<PostImage> images) {
        SimplePostResponse response = new SimplePostResponse();
        response.setAuthorId(author.getId());
        response.setAuthorName(author.getUsername());
        response.setAuthorAvatar(author.getAvatarUrl());

        response.setPostId(post.getId());
        response.setTitle(post.getTitle());
        response.setSummary(buildSummary(post.getContent()));
        response.setLikeCount(post.getLikeCount());
        response.setCommentCount(post.getCommentCount());

        response.setPreviewImages(
                images == null ? List.of()
                        : images.stream()
                        .map(PostImageResponse::toDTO)
                        .toList()
        );
        return response;
    }

    private static String buildSummary(String content) {
        if (content == null) return "";
        return content.length() > 100
                ? content.substring(0, 100) + "..."
                : content;
    }
}
