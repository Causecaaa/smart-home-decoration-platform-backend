package org.homedecoration.post.post.dto.response;

import lombok.Data;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.post.image.dto.response.PostImageResponse;
import org.homedecoration.post.image.entity.PostImage;
import org.homedecoration.post.post.entity.Post;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DetailPostResponse {

    /* ========= 文章 ========= */
    private Long postId;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    /* ========= 作者 ========= */
    private Long authorId;
    private String authorName;
    private String authorAvatar;

    /* ========= 统计 ========= */
    private int likeCount;
    private int commentCount;
    private boolean liked;   // 当前用户是否点赞过

    /* ========= 扩展 ========= */
    private List<PostImageResponse> previewImages;
    // private List<CommentResponse> comments;

    public static DetailPostResponse toDTO(Post post,
                                          User author,
                                          boolean liked,
                                           List<PostImage> images) {
        DetailPostResponse response = new DetailPostResponse();

        response.setPostId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setCreatedAt(post.getCreatedAt());

        response.setAuthorId(author.getId());
        response.setAuthorName(author.getUsername());
        response.setAuthorAvatar(author.getAvatarUrl());

        response.setLikeCount(post.getLikeCount());
        response.setCommentCount(post.getCommentCount());
        response.setLiked(liked);

        response.setPreviewImages(
                images == null ? List.of()
                        : images.stream()
                        .map(PostImageResponse::toDTO)
                        .toList()
        );

        return response;
    }
}
