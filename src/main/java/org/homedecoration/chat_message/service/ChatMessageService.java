package org.homedecoration.chat_message.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.homedecoration.chat_message.dto.request.ChatMessageRequest;
import org.homedecoration.chat_message.entity.ChatMessage;
import org.homedecoration.chat_message.repository.ChatMessageRepository;
import org.homedecoration.identity.user.dto.response.UserResponse;
import org.homedecoration.identity.user.repository.UserRepository;
import org.homedecoration.identity.user.service.UserService;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final UserRepository userRepository;
    private final UserService userService;
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final ChatMessageRepository chatMessageRepository;

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

        return chatMessageRepository.save(message);
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

        return chatMessageRepository.save(message);
    }

    @Transactional
    public List<ChatMessage> getConversation(Long myId, Long otherId) {
        // 查询两个人之间的所有消息
        return chatMessageRepository
                .findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
                        myId, otherId,
                        myId, otherId
                );
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

        // 删除消息
        chatMessageRepository.delete(message);
    }




    // 在 ChatMessageService 中
    @Transactional
    public List<UserResponse> getChatPartnersByCurrentUser(Long userId) {
        List<Object[]> results = chatMessageRepository.findDistinctChatUserIdsByUserId(userId);

        List<Long> partnerIds = results.stream()
                .map(result -> ((Number) result[0]).longValue())
                .collect(Collectors.toList());

        // 根据用户ID获取用户详细信息
        return partnerIds.stream()
                .map(userService::getById) // 假设UserService有此方法
                .map(UserResponse::toDTO) // 将User实体转换为UserResponse DTO
                .collect(Collectors.toList());
    }

}
