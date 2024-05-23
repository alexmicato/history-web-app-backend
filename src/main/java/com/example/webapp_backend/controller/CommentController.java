package com.example.webapp_backend.controller;

import com.example.webapp_backend.config.util.SecurityUtil;
import com.example.webapp_backend.exception.CustomException;
import com.example.webapp_backend.model.CommentEntity;
import com.example.webapp_backend.model.PostEntity;
import com.example.webapp_backend.model.UserEntity;
import com.example.webapp_backend.model.dto.CommentDTO;
import com.example.webapp_backend.model.dto.DTOutils;
import com.example.webapp_backend.service.CommentService;
import com.example.webapp_backend.service.PostService;
import com.example.webapp_backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final PostService postService;
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    public CommentController(CommentService commentService, UserService userService, PostService postService) {
        this.commentService = commentService;
        this.userService = userService;
        this.postService = postService;
    }

    @PostMapping("/comments")
    public ResponseEntity<CommentDTO> addComment(@RequestBody CommentDTO commentDTO, @RequestParam("postId") Long postId) {
        String username = SecurityUtil.getAuthenticatedUsername();
        UserEntity user = userService.findByUsername(username);
        PostEntity post = postService.findPostById(postId)
                .orElseThrow(() -> new IllegalStateException("Post not found in database"));

        CommentEntity comment = new CommentEntity();
        comment.setContent(commentDTO.getContent());
        comment.setUser(user);
        comment.setPost(post);
        CommentEntity savedComment = commentService.addComment(comment);

        CommentDTO newCommentDTO = DTOutils.convertToCommentDTO(savedComment);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCommentDTO);
    }

    @GetMapping("/comments/post/{id}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPost(@PathVariable("id") Long postId) {
        List<CommentEntity> comments = commentService.getCommentsByPost(postId);
        List<CommentDTO> commentDTOs = comments.stream()
                .map(DTOutils::convertToCommentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentDTOs);
    }



    @PutMapping("/comments/{commentId}")
    //@PreAuthorize("hasRole('MODERATOR') or @commentAuthorizationService.canEditComment(#commentId, principal.username)")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, @RequestBody CommentDTO commentDTO) {
        String username = SecurityUtil.getAuthenticatedUsername();
        UserEntity currentUser = userService.findByUsername(username);
        CommentEntity comment = commentService.findCommentById(commentId);

        if (!comment.getUser().equals(currentUser) && !SecurityUtil.isModerator(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        comment.setContent(commentDTO.getContent());
        CommentEntity updatedComment = commentService.updateComment(comment);
        return ResponseEntity.ok(DTOutils.convertToCommentDTO(updatedComment));
    }

    @DeleteMapping("/comments/{commentId}")
    //@PreAuthorize("hasRole('MODERATOR') or @commentAuthorizationService.canEditComment(#commentId, principal.username)")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        String username = SecurityUtil.getAuthenticatedUsername();
        UserEntity currentUser = userService.findByUsername(username);
        CommentEntity comment = commentService.findCommentById(commentId);

        if (!comment.getUser().equals(currentUser) && !SecurityUtil.isModerator(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/comments/{id}/isOwner")
    public ResponseEntity<Boolean> isCommentOwner(@PathVariable Long id) {
        String username = SecurityUtil.getAuthenticatedUsername();
        boolean isOwner = commentService.isUserCommentOwner(id, username);
        return ResponseEntity.ok(isOwner);
    }

}
