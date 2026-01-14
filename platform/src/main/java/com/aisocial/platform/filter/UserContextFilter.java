package com.aisocial.platform.filter;

import com.aisocial.platform.context.UserContext;
import com.aisocial.platform.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter that extracts the X-User-Id header from requests
 * and sets the current user in UserContext.
 */
@Component
public class UserContextFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String userIdHeader = request.getHeader("X-User-Id");

            if (userIdHeader != null && !userIdHeader.trim().isEmpty()) {
                try {
                    UUID userId = UUID.fromString(userIdHeader.trim());
                    userRepository.findById(userId)
                            .ifPresent(UserContext::setCurrentUser);
                } catch (IllegalArgumentException e) {
                    // Invalid UUID format - ignore and continue without user context
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            // Always clear context after request to prevent memory leaks
            UserContext.clear();
        }
    }
}