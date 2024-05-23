package com.example.webapp_backend.config.util;

import com.example.webapp_backend.model.UserEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {


    public static String getAuthenticatedUsername()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("No authenticated user available");
        }
        return authentication.getName();
    }

    public static UserEntity getAuthenticatedUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("No authenticated user available");
        }
        return (UserEntity) authentication.getPrincipal();
    }

    public static boolean isModerator(UserEntity user) {
        return user.getRoleEntities().stream()
                .anyMatch(role -> "MODERATOR".equals(role.getName()));
    }
}
