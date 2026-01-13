package com.aisocial.platform.filter;

import com.aisocial.platform.context.UserContext;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserContextFilter Tests")
class UserContextFilterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private UserContextFilter userContextFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private User testUser;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        
        testUser = new User("testuser", "Test User", "Bio");
        testUser.setId(UUID.randomUUID());
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Nested
    @DisplayName("When X-User-Id header is present")
    class WhenHeaderPresent {

        @Test
        @DisplayName("Should set current user in context when valid UUID")
        void shouldSetCurrentUserWhenValidUUID() throws ServletException, IOException {
            request.addHeader("X-User-Id", testUser.getId().toString());
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

            userContextFilter.doFilterInternal(request, response, filterChain);

            // Note: Context is cleared in finally block, so we verify via mock
            verify(userRepository).findById(testUser.getId());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should call filter chain even with valid user")
        void shouldCallFilterChain() throws ServletException, IOException {
            request.addHeader("X-User-Id", testUser.getId().toString());
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

            userContextFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should not set user when UUID not found in database")
        void shouldNotSetUserWhenNotFound() throws ServletException, IOException {
            UUID unknownId = UUID.randomUUID();
            request.addHeader("X-User-Id", unknownId.toString());
            when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

            userContextFilter.doFilterInternal(request, response, filterChain);

            verify(userRepository).findById(unknownId);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should handle invalid UUID format gracefully")
        void shouldHandleInvalidUUIDFormat() throws ServletException, IOException {
            request.addHeader("X-User-Id", "not-a-valid-uuid");

            userContextFilter.doFilterInternal(request, response, filterChain);

            verify(userRepository, never()).findById(any());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should handle empty UUID string")
        void shouldHandleEmptyUUIDString() throws ServletException, IOException {
            request.addHeader("X-User-Id", "");

            userContextFilter.doFilterInternal(request, response, filterChain);

            verify(userRepository, never()).findById(any());
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("When X-User-Id header is missing")
    class WhenHeaderMissing {

        @Test
        @DisplayName("Should not query database when header missing")
        void shouldNotQueryDatabaseWhenHeaderMissing() throws ServletException, IOException {
            // No header added

            userContextFilter.doFilterInternal(request, response, filterChain);

            verify(userRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should still call filter chain when header missing")
        void shouldCallFilterChainWhenHeaderMissing() throws ServletException, IOException {
            // No header added

            userContextFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should not set user in context when header missing")
        void shouldNotSetUserWhenHeaderMissing() throws ServletException, IOException {
            // No header added

            userContextFilter.doFilterInternal(request, response, filterChain);

            // Context should remain empty (cleared in finally)
            assertNull(UserContext.getCurrentUser());
        }
    }

    @Nested
    @DisplayName("Context cleanup")
    class ContextCleanup {

        @Test
        @DisplayName("Should clear context after successful request")
        void shouldClearContextAfterSuccess() throws ServletException, IOException {
            request.addHeader("X-User-Id", testUser.getId().toString());
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

            userContextFilter.doFilterInternal(request, response, filterChain);

            // After filter completes, context should be cleared
            assertNull(UserContext.getCurrentUser());
        }

        @Test
        @DisplayName("Should clear context even when filter chain throws exception")
        void shouldClearContextOnException() throws ServletException, IOException {
            request.addHeader("X-User-Id", testUser.getId().toString());
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
            doThrow(new ServletException("Test exception")).when(filterChain).doFilter(request, response);

            assertThrows(ServletException.class, () -> {
                userContextFilter.doFilterInternal(request, response, filterChain);
            });

            // Context should still be cleared even after exception
            assertNull(UserContext.getCurrentUser());
        }

        @Test
        @DisplayName("Should clear context even when repository throws exception")
        void shouldClearContextOnRepositoryException() throws ServletException, IOException {
            request.addHeader("X-User-Id", testUser.getId().toString());
            when(userRepository.findById(testUser.getId())).thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class, () -> {
                userContextFilter.doFilterInternal(request, response, filterChain);
            });

            assertNull(UserContext.getCurrentUser());
        }
    }

    @Nested
    @DisplayName("User context during request")
    class UserContextDuringRequest {

        @Test
        @DisplayName("Should make user available during filter chain execution")
        void shouldMakeUserAvailableDuringFilterChain() throws ServletException, IOException {
            request.addHeader("X-User-Id", testUser.getId().toString());
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

            // Capture what user is available during filterChain.doFilter()
            doAnswer(invocation -> {
                // This runs during the request - user should be set
                User currentUser = UserContext.getCurrentUser();
                assertNotNull(currentUser, "User should be available during request");
                assertEquals(testUser.getId(), currentUser.getId());
                assertEquals("testuser", currentUser.getUsername());
                return null;
            }).when(filterChain).doFilter(request, response);

            userContextFilter.doFilterInternal(request, response, filterChain);
        }

        @Test
        @DisplayName("Should have no user during filter chain when header missing")
        void shouldHaveNoUserDuringFilterChainWhenHeaderMissing() throws ServletException, IOException {
            // No header

            doAnswer(invocation -> {
                assertNull(UserContext.getCurrentUser(), "User should be null when no header");
                return null;
            }).when(filterChain).doFilter(request, response);

            userContextFilter.doFilterInternal(request, response, filterChain);
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle whitespace-only UUID header")
        void shouldHandleWhitespaceOnlyHeader() throws ServletException, IOException {
            request.addHeader("X-User-Id", "   ");

            userContextFilter.doFilterInternal(request, response, filterChain);

            verify(userRepository, never()).findById(any());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should handle UUID with extra whitespace by trimming")
        void shouldHandleUUIDWithWhitespace() throws ServletException, IOException {
            // UUIDs with whitespace should be trimmed and parsed successfully
            request.addHeader("X-User-Id", "  " + testUser.getId().toString() + "  ");
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

            userContextFilter.doFilterInternal(request, response, filterChain);

            // Should trim and find the user
            verify(userRepository).findById(testUser.getId());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should handle multiple X-User-Id headers (uses first)")
        void shouldHandleMultipleHeaders() throws ServletException, IOException {
            User secondUser = new User("second", "Second User", "Bio 2");
            secondUser.setId(UUID.randomUUID());
            
            request.addHeader("X-User-Id", testUser.getId().toString());
            request.addHeader("X-User-Id", secondUser.getId().toString());
            
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

            userContextFilter.doFilterInternal(request, response, filterChain);

            // Should use first header value
            verify(userRepository).findById(testUser.getId());
            verify(userRepository, never()).findById(secondUser.getId());
        }
    }
}