package org.homedecoration.chat_session.dto;

import lombok.Data;
import org.homedecoration.chat_session.entity.ChatSession;
import org.homedecoration.identity.user.entity.User;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Objects;

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

        dto.setLastMessageContent(buildLastMessagePreview(session));
        dto.setLastMessageTime(session.getLastMessageTime());

        // unread 判断逻辑
        if (session.getLastReadTime() == null) {
            dto.setUnread(true);
        } else dto.setUnread(session.getLastMessageTime() != null &&
                session.getLastMessageTime().isAfter(session.getLastReadTime()));

        return dto;
    }

    private static final int MAX_TEXT_PREVIEW_LENGTH = 20;

    private static String buildLastMessagePreview(ChatSession session) {
        if (Objects.equals(session.getLastMessageType(), "IMAGE")) {
            return "[图片]";
        }

        if (Objects.equals(session.getLastMessageType(), "TEXT")) {
            String content = session.getLastMessageContent();
            if (content == null) return "";

            return content.length() > MAX_TEXT_PREVIEW_LENGTH
                    ? content.substring(0, MAX_TEXT_PREVIEW_LENGTH) + "…"
                    : content;
        }

        return "";
    }

}
