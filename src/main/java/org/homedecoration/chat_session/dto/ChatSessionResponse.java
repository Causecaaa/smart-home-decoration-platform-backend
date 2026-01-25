package org.homedecoration.chat_session.dto;

import lombok.Data;
import org.homedecoration.chat_session.entity.ChatSession;
import org.homedecoration.identity.user.entity.User;

import java.time.LocalDateTime;

@Data
public class ChatSessionResponse {

    private Long partnerId;
    private String partnerName;
    private String partnerAvatar;

    private String lastMessageContent;
    private LocalDateTime lastMessageTime;

    private Boolean unread;

    public static ChatSessionResponse toDTO(User user, ChatSession session) {
        ChatSessionResponse dto = new ChatSessionResponse();

        dto.setPartnerId(session.getPartnerId());
        dto.setPartnerName(user.getUsername());
        dto.setPartnerAvatar(user.getAvatarUrl());

        dto.setLastMessageContent(session.getLastMessageContent());
        dto.setLastMessageTime(session.getLastMessageTime());

        // unread 判断逻辑
        if (session.getLastReadTime() == null) {
            dto.setUnread(true);
        } else dto.setUnread(session.getLastMessageTime() != null &&
                session.getLastMessageTime().isAfter(session.getLastReadTime()));

        return dto;
    }
}
