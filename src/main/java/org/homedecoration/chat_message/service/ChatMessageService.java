package org.homedecoration.chat_message.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.homedecoration.chat_message.dto.request.ChatMessageRequest;
import org.homedecoration.chat_message.entity.ChatMessage;
import org.homedecoration.chat_message.repository.ChatMessageRepository;
import org.homedecoration.chat_session.dto.ChatSessionResponse;
import org.homedecoration.chat_session.entity.ChatSession;
import org.homedecoration.chat_session.repository.ChatSessionRepository;
import org.homedecoration.identity.user.dto.response.UserResponse;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.identity.user.repository.UserRepository;
import org.homedecoration.identity.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final UserRepository userRepository;
    private final UserService userService;
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;

    private void updateOrCreateSession(Long userId, Long partnerId, String content, String type, LocalDateTime lastReadTime) {
        ChatSession session = chatSessionRepository
                .findByUserIdAndPartnerId(userId, partnerId)
                .orElseGet(() -> {
                    ChatSession s = new ChatSession();
                    s.setUserId(userId);
                    s.setPartnerId(partnerId);
                    return s;
                });

        session.setLastMessageContent(content);
        session.setLastMessageType(type);
        session.setLastMessageTime(LocalDateTime.now());
        session.setLastReadTime(lastReadTime);

        chatSessionRepository.save(session);
    }


    @Transactional
    public ChatMessage sendTextMessage(ChatMessageRequest request) {
        userService.getById(request.getReceiverId());

        if (request.getText() == null || request.getText().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文本内容不能为空");
        }

        ChatMessage message = new ChatMessage();
        message.setSenderId(request.getSenderId());
        message.setReceiverId(request.getReceiverId());
        message.setContent(request.getText());
        message.setContentType(ChatMessage.ContentType.TEXT);

        chatMessageRepository.save(message);

        updateOrCreateSession(request.getSenderId(), request.getReceiverId(), message.getContent(), message.getContentType().name(), LocalDateTime.now());
        updateOrCreateSession(request.getReceiverId(), request.getSenderId(), message.getContent(), message.getContentType().name(), null);

        return message;
    }




    @Transactional
    public ChatMessage sendImageMessage(Long senderId, Long receiverId, MultipartFile file) throws IOException {
        userService.getById(receiverId);

        String originalName = file.getOriginalFilename();
        String filename = System.currentTimeMillis() + "_" + originalName;
        Path path = Paths.get(uploadDir + "/chat", filename);
        Files.createDirectories(path.getParent());
        file.transferTo(path.toFile());

        ChatMessage message = new ChatMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent("/uploads/chat/" + filename);
        message.setContentType(ChatMessage.ContentType.IMAGE);

        chatMessageRepository.save(message);

        updateOrCreateSession(senderId, receiverId, message.getContent(), message.getContentType().name(), LocalDateTime.now());
        updateOrCreateSession(receiverId, senderId, message.getContent(), message.getContentType().name(), null);

        return message;
    }

    @Transactional
    public List<ChatMessage> getConversation(Long myId, Long otherId) {
        // 查询两个人之间的所有消息
        List<ChatMessage> messages = chatMessageRepository
                .findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
                        myId, otherId,
                        myId, otherId
                );

        chatSessionRepository.findByUserIdAndPartnerId(myId, otherId).ifPresent(session -> {
            session.setLastReadTime(LocalDateTime.now());
            chatSessionRepository.save(session);
        });

        return messages;
    }


    @Transactional
    public void deleteMessage(Long messageId, Long requesterId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "消息不存在"));

        if (!message.getSenderId().equals(requesterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只能删除自己的消息");
        }

        Instant now = Instant.now();
        Duration duration = Duration.between(message.getTimestamp(), now);
        if (duration.toMinutes() > 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "发送超过三分钟的消息不能删除");
        }

        Long senderId = message.getSenderId();
        Long receiverId = message.getReceiverId();

        // 删除消息
        chatMessageRepository.delete(message);

        // 查找最后一条消息（Optional 安全处理）
        Optional<ChatMessage> lastMsgOpt = chatMessageRepository
                .findTopBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampDesc(
                        senderId, receiverId, senderId, receiverId
                );

        String lastContent = null;
        String lastType = null;
        LocalDateTime lastTime = null;

        if (lastMsgOpt.isPresent()) {
            ChatMessage lastMsg = lastMsgOpt.get();
            lastContent = lastMsg.getContent();
            lastType = lastMsg.getContentType().name();
            lastTime = LocalDateTime.from(lastMsg.getTimestamp());
        }

        // 更新双方会话
        updateOrCreateSession(senderId, receiverId, lastContent, lastType, lastTime);  // 发送方已读
        updateOrCreateSession(receiverId, senderId, lastContent, lastType, null);       // 接收方未读
    }




    @Transactional
    public List<ChatSessionResponse> getChatPartnersWithSessionInfo(Long userId) {
        // 获取用户的所有会话
        List<ChatSession> sessions = chatSessionRepository.findByUserIdOrderByLastMessageTimeDesc(userId);

        return sessions.stream()
                .map(session -> {
                    User partner = userService.getById(session.getPartnerId());
                    return ChatSessionResponse.toDTO(partner, session);
                })
                .collect(Collectors.toList());
    }

}
