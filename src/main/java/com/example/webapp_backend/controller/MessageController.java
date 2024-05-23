package com.example.webapp_backend.controller;

import com.example.webapp_backend.config.util.SecurityUtil;
import com.example.webapp_backend.model.MessageEntity;
import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.model.dto.ChatPreviewDTO;
import com.example.webapp_backend.model.dto.MessageDTO;
import com.example.webapp_backend.model.dto.DTOutils;
import com.example.webapp_backend.service.MessageService;

import com.example.webapp_backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;
    private final SimpMessagingTemplate template;

    public MessageController(MessageService messageService, UserService userService, SimpMessagingTemplate template) {
        this.messageService = messageService;
        this.userService = userService;
        this.template = template;
    }

    @MessageMapping("/messages/send")
    public void handleSendMessage(@Payload MessageDTO messageDTO, SimpMessageHeaderAccessor headerAccessor,
                                  Principal principal) {

        if (principal == null) {
            throw new IllegalStateException("Principal must not be null");
        }

        System.out.println("Received message to send: " + messageDTO.getContent());

        UserEntity sender = userService.findByUsername(messageDTO.getSender());
        System.out.println("Sender: " + sender.getUsername());

        UserEntity receiver = userService.findByUsername(messageDTO.getReceiver());
        System.out.println("Receiver: " + receiver.getUsername());

        if (sender == null || receiver == null) {
            throw new IllegalStateException("Sender or receiver not found");
        }

        if (!principal.getName().equals(sender.getUsername())) {
            throw new IllegalStateException("Not allowed");
        }

        if(sender.getUsername().equals(receiver.getUsername()))
        {
            throw new IllegalStateException("Cannot send message to yourself");
        }

        // Prepare and send the message entity
        MessageEntity message = new MessageEntity();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(messageDTO.getContent());
        message.setSentAt(new Date());

        // Save the message and broadcast it via WebSocket to the receiver
        MessageEntity sentMessage = messageService.sendMessage(message);
        System.out.println("Sending message to " + receiver.getUsername() + " at /queue/messages");

        MessageDTO sentMessageDTO = DTOutils.convertToMessageDTO(sentMessage);

        template.convertAndSendToUser(receiver.getUsername(), "/queue/messages", sentMessageDTO);

        ChatPreviewDTO chatPreview = new ChatPreviewDTO(receiver.getUsername(), new Date());
        template.convertAndSend("/topic/chats", chatPreview);

    }


    @PostMapping("/messages/send")
    public ResponseEntity<MessageEntity> sendMessage(@RequestBody MessageDTO messageDTO, Principal principal) {
        UserEntity sender = userService.findByUsername(principal.getName());
        UserEntity receiver = userService.findByUsername(messageDTO.getReceiver());

        if (sender == null || receiver == null) {
            throw new IllegalStateException("Sender or receiver not found");
        }

        if (!principal.getName().equals(sender.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if(sender.getUsername().equals(receiver.getUsername()))
        {
            throw new IllegalStateException("Cannot send message to himself");
        }

        MessageEntity message = new MessageEntity();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(messageDTO.getContent());
        message.setSentAt(new Date());

        MessageEntity sentMessage = messageService.sendMessage(message);

        template.convertAndSendToUser(receiver.getUsername(), "/queue/messages", message);

        return ResponseEntity.ok(sentMessage);
    }

    @GetMapping("/messages/conversation")
    public ResponseEntity<List<MessageDTO>> getConversation(
            @RequestParam String senderUsername,
            @RequestParam String receiverUsername) {

        UserEntity senderUser = userService.findByUsername(senderUsername);
        UserEntity receiverUser = userService.findByUsername(receiverUsername);

        if (senderUser == null || receiverUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One of the users not found");
        }

        Long currentUserId = senderUser.getId();
        Long otherUserId = receiverUser.getId();

        List<MessageEntity> conversation = messageService.getConversation(currentUserId, otherUserId);

        List<MessageDTO> conversationDTO = conversation.stream()
                .map(DTOutils::convertToMessageDTO)
                .sorted(Comparator.comparing(MessageDTO::getSentAt))
                .collect(Collectors.toList());

        return ResponseEntity.ok(conversationDTO);
    }

    @GetMapping("/messages/chats/{username}")
    public ResponseEntity<List<ChatPreviewDTO>> getUserChats(@PathVariable String username) {
        UserEntity user = userService.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        List<ChatPreviewDTO> chatPreviews = messageService.getUserChats(user.getId());
        return ResponseEntity.ok(chatPreviews);
    }

    // Get all messages received by a user
    @GetMapping("/messages/received/{userId}")
    public ResponseEntity<List<MessageEntity>> getReceivedMessages(@PathVariable Long userId) {
        List<MessageEntity> messages = messageService.getReceivedMessages(userId);
        return ResponseEntity.ok(messages);
    }

    // Get all messages sent by a user
    @GetMapping("/messages/sent/{userId}")
    public ResponseEntity<List<MessageEntity>> getSentMessages(@PathVariable Long userId) {
        List<MessageEntity> messages = messageService.getSentMessages(userId);
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build(); // Returns a 204 No Content status
    }

}