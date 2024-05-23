package com.example.webapp_backend.config;

import com.example.webapp_backend.model.data.Roles;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        logger.info("Authentication successful");
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        logger.info("User authorities: {}", authorities);

        if (authorities.contains(new SimpleGrantedAuthority(Roles.ADMIN.name()))) {
            logger.info("redirecting to admin endpoint");
            response.sendRedirect("/admin");
        } else if (authorities.contains(new SimpleGrantedAuthority(Roles.USER.name()))) {
            logger.info("redirecting to home endpoint");
            response.sendRedirect("/user");
        } else {
            logger.error("current user does not have a proper role");
            throw new IllegalStateException("Unexpected role: " + authorities);
        }
    }
}
