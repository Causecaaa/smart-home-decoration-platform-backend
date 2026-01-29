package org.homedecoration.post.image.dto.response;

import lombok.Data;
import org.homedecoration.post.image.entity.PostImage;

import java.time.LocalDateTime;

@Data
public class PostImageResponse {

    private Long id;
    private String imageUrl;

    public static PostImageResponse toDTO(PostImage image) {
        PostImageResponse res = new PostImageResponse();
        res.setId(image.getId());
        res.setImageUrl(image.getImageUrl());

        return res;
    }
}
