package org.homedecoration.chat_message.dto.response;

import lombok.Data;
import org.homedecoration.chat_message.entity.ChatMessage;

import java.time.Instant;

@Data
public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private ChatMessage.ContentType contentType;
    private Instant timestamp;

    public static ChatMessageResponse toDTO(ChatMessage entity) {
        ChatMessageResponse dto = new ChatMessageResponse();
        dto.setId(entity.getId());
        dto.setSenderId(entity.getSenderId());
        dto.setReceiverId(entity.getReceiverId());
        dto.setContent(entity.getContent());
        dto.setContentType(entity.getContentType());
        dto.setTimestamp(entity.getTimestamp());
        return dto;
    }
}
