package org.homedecoration.post.image.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "post_image")
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 所属文章 ID
     */
    @Column(name = "post_id", nullable = false)
    private Long postId;

    /**
     * 图片访问地址
     */
    @Column(name = "image_url", nullable = false, length = 200)
    private String imageUrl;


    /**
     * 创建时间
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
