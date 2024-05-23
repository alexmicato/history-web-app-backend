package com.example.webapp_backend.repository;

import com.example.webapp_backend.model.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long>
{
    List<MessageEntity> findByReceiverId(Long receiverId);
    List<MessageEntity> findBySenderId(Long senderId);

    @Query("SELECT m FROM MessageEntity m WHERE (m.sender.id = :userId1 AND m.receiver.id = :userId2) OR (m.sender.id = :userId2 AND m.receiver.id = :userId1) ORDER BY m.sentAt ASC")
    List<MessageEntity> findConversationBetweenTwoUsers(Long userId1, Long userId2);

    @Query("SELECT m FROM MessageEntity m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    List<MessageEntity> findUserChatsByUserId(@Param("userId") Long userId);


}