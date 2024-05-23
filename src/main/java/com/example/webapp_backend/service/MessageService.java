package com.example.webapp_backend.service;

import com.example.webapp_backend.exception.CustomException;
import com.example.webapp_backend.model.MessageEntity;
import com.example.webapp_backend.model.dto.ChatPreviewDTO;
import com.example.webapp_backend.repository.MessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public MessageEntity sendMessage(MessageEntity message) {

        validateMessage(message);

        return messageRepository.save(message);
    }

    private void validateMessage(MessageEntity message) {
        if (message.getSender() == null || message.getReceiver() == null) {
            throw new CustomException("Sender and receiver cannot be null.");
        }
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            throw new CustomException("Message content cannot be empty.");
        }
    }

    // Get all messages received by a specific user
    public List<MessageEntity> getReceivedMessages(Long userId) {
        return messageRepository.findByReceiverId(userId);
    }

    // Get all messages sent by a specific user
    public List<MessageEntity> getSentMessages(Long userId) {
        return messageRepository.findBySenderId(userId);
    }

    @Transactional
    public List<MessageEntity> getConversation(Long userId1, Long userId2) {
        return messageRepository.findConversationBetweenTwoUsers(userId1, userId2);
    }

    // Delete a specific message by ID
    public void deleteMessage(Long messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new RuntimeException("Message not found with ID: " + messageId);
        }
        messageRepository.deleteById(messageId);
    }

    @Transactional
    public List<ChatPreviewDTO> getUserChats(Long userId) {
        List<ChatPreviewDTO> previews = buildChatPreviews(userId);
        System.out.println("Chat Previews: " + previews); // Log the previews to check
        return previews;
    }

    // Helper method to build chat previews within a transaction
    private List<ChatPreviewDTO> buildChatPreviews(Long userId) {
        List<MessageEntity> messages = messageRepository.findUserChatsByUserId(userId);
        Map<String, Date> lastMessages = new HashMap<>();
        for (MessageEntity message : messages) {
            String chatPartnerUsername = (message.getSender().getId().equals(userId)) ? message.getReceiver().getUsername() : message.getSender().getUsername();
            Date currentMessageDate = message.getSentAt();
            if (!lastMessages.containsKey(chatPartnerUsername) || lastMessages.get(chatPartnerUsername).before(currentMessageDate)) {
                lastMessages.put(chatPartnerUsername, currentMessageDate);
            }
        }
        return lastMessages.entrySet().stream()
                .map(e -> new ChatPreviewDTO(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(ChatPreviewDTO::getLastMessageAt).reversed())
                .collect(Collectors.toList());
    }
}
