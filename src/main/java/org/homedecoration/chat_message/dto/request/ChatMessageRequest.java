package org.homedecoration.chat_message.dto.request;

import lombok.Data;

@Data
public class ChatMessageRequest {
    private Long senderId;
    private Long receiverId;
    private String text;         // 文本消息，可选
    private String imageUrl;     // 图片消息，可选（可由前端上传返回URL）
}
