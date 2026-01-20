package org.homedecoration.chat_message.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homedecoration.chat_message.dto.request.ChatMessageRequest;
import org.homedecoration.chat_message.dto.response.ChatMessageResponse;
import org.homedecoration.chat_message.entity.ChatMessage;
import org.homedecoration.chat_message.service.ChatMessageService;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.identity.user.dto.response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final JwtUtil jwtUtil;

    @PostMapping("/text-create")
    public ApiResponse<ChatMessageResponse> sendTextMessage(
            @RequestBody @Valid ChatMessageRequest request,
            HttpServletRequest httpRequest
    ) throws IOException {
        request.setSenderId(jwtUtil.getUserId(httpRequest));

        if (request.getText() == null || request.getText().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文本内容不能为空");
        }

        ChatMessage message = chatMessageService.sendTextMessage(request);
        return ApiResponse.success(ChatMessageResponse.toDTO(message));
    }

    @PostMapping("/image-create/{receiverId}")
    public ApiResponse<ChatMessageResponse> sendImageMessage(
            @PathVariable("receiverId") Long receiverId,
            @RequestPart("file") MultipartFile file,
            HttpServletRequest httpRequest
    ) throws IOException {

        Long senderId = jwtUtil.getUserId(httpRequest);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("必须上传图片");
        }

        ChatMessage message = chatMessageService.sendImageMessage(senderId, receiverId, file);
        return ApiResponse.success(ChatMessageResponse.toDTO(message));
    }


    @GetMapping("/conversation/{otherId}")
    public ApiResponse<List<ChatMessageResponse>> getConversation(
            @PathVariable Long otherId,
            HttpServletRequest httpRequest
    ) {
        Long myId = jwtUtil.getUserId(httpRequest);
        List<ChatMessage> messages = chatMessageService.getConversation(myId, otherId);

        List<ChatMessageResponse> dtoList = messages.stream()
                .map(ChatMessageResponse::toDTO)
                .collect(Collectors.toList());

        return ApiResponse.success(dtoList);
    }

    @DeleteMapping("/delete/{messageId}")
    public ApiResponse<String> deleteMessage(
            @PathVariable Long messageId,
            HttpServletRequest httpRequest
    ) {
        Long userId = jwtUtil.getUserId(httpRequest);
        chatMessageService.deleteMessage(messageId, userId);
        return ApiResponse.success( "消息删除成功");
    }

    @GetMapping("/chat-partners")
    public ApiResponse<List<UserResponse>> getChatPartners(HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserId(httpRequest);
        List<UserResponse> partners = chatMessageService.getChatPartnersByCurrentUser(userId);
        return ApiResponse.success(partners);
    }

}
