package com.example.webapp_backend.unitTests.serviceTests;
import com.example.webapp_backend.exception.CustomException;
import com.example.webapp_backend.model.MessageEntity;
import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.service.MessageService;
import com.example.webapp_backend.model.dto.ChatPreviewDTO;
import com.example.webapp_backend.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendMessage_Success() {
        MessageEntity message = new MessageEntity();
        UserEntity sender = new UserEntity();
        UserEntity receiver = new UserEntity();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent("Hello, world!");

        when(messageRepository.save(any(MessageEntity.class))).thenReturn(message);

        MessageEntity sentMessage = messageService.sendMessage(message);
        assertNotNull(sentMessage);
        verify(messageRepository).save(message);
    }

    @Test
    void sendMessage_ValidationFailure() {
        MessageEntity message = new MessageEntity(); // Missing sender, receiver, and content

        Exception exception = assertThrows(CustomException.class, () -> messageService.sendMessage(message));
        assertTrue(exception.getMessage().contains("Sender and receiver cannot be null"));
    }

    @Test
    void getReceivedMessages_Success() {
        Long userId = 1L;
        List<MessageEntity> messages = Arrays.asList(new MessageEntity(), new MessageEntity());

        when(messageRepository.findByReceiverId(userId)).thenReturn(messages);

        List<MessageEntity> receivedMessages = messageService.getReceivedMessages(userId);
        assertEquals(2, receivedMessages.size());
        verify(messageRepository).findByReceiverId(userId);
    }

    @Test
    void deleteMessage_Success() {
        Long messageId = 1L;
        when(messageRepository.existsById(messageId)).thenReturn(true);
        doNothing().when(messageRepository).deleteById(messageId);

        assertDoesNotThrow(() -> messageService.deleteMessage(messageId));
        verify(messageRepository).deleteById(messageId);
    }

    @Test
    void deleteMessage_NotFound() {
        Long messageId = 1L;
        when(messageRepository.existsById(messageId)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> messageService.deleteMessage(messageId));
        assertEquals("Message not found with ID: " + messageId, exception.getMessage());
    }

    @Test
    void getUserChats_Success() {
        Long userId = 1L;
        when(messageRepository.findUserChatsByUserId(userId)).thenReturn(Collections.emptyList()); // Simplified for the test

        List<ChatPreviewDTO> previews = messageService.getUserChats(userId);
        assertNotNull(previews);
        assertTrue(previews.isEmpty());
        verify(messageRepository).findUserChatsByUserId(userId);
    }

}
