package org.homedecoration.post.post.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 作者用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 文章标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 文章正文
     */
    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    /**
     * 点赞数（冗余字段）
     */
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    /**
     * 评论数（冗余字段）
     */
    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;

    /**
     * 创建时间
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
