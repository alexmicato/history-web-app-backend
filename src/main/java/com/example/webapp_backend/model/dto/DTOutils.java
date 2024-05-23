package com.example.webapp_backend.model.dto;

import com.example.webapp_backend.model.CommentEntity;
import com.example.webapp_backend.model.MessageEntity;
import com.example.webapp_backend.model.PostEntity;
import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.model.RoleEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DTOutils {

    public static PostDTO convertToPostDTO(PostEntity post) {
        return new PostDTO(
                post.getId(),
                post.getUser().getUsername(), // Ensure the user is never null as per your domain model
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getComments().size(),
                post.getCategory() != null ? post.getCategory().getUserFriendlyName() : "Uncategorized",
                post.getLikes().size()
        );
    }

    public static CommentDTO convertToCommentDTO(CommentEntity comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getUsername(),
                comment.getCreatedAt()
        );
    }

    public static MessageDTO convertToMessageDTO(MessageEntity messageEntity) {
        MessageDTO message = new MessageDTO(
                messageEntity.getSender().getUsername(),
                messageEntity.getReceiver().getUsername(),
                messageEntity.getContent()
        );

        message.setSentAt(messageEntity.getSentAt());
        return message;
    }

    public static UserDTO convertToUserDTO(UserEntity user) {
        List<String> roles = user.getRoleEntities().stream()
                .map(RoleEntity::getName) // Assuming Role entity has a getName method
                .collect(Collectors.toList());
        return new UserDTO(user.getUsername(), roles);
    }

}
