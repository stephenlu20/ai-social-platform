package com.aisocial.platform.service;

import com.aisocial.platform.entity.Post;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.PostRepository;
import com.aisocial.platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Post Service Tests")
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private User author;

    @BeforeEach
    void setUp() {
        author = new User("author", "Author Name", "Bio");
        author.setId(UUID.randomUUID());
    }

    @Nested
    @DisplayName("Create Post Tests")
    class CreatePostTests {

        @Test
        @DisplayName("Should create and save a post successfully")
        void shouldCreatePost() {
            String content = "Hello world";

            when(userRepository.findById(author.getId()))
                    .thenReturn(Optional.of(author));

            when(postRepository.save(any(Post.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Post result = postService.createPost(author.getId(), content);

            assertNotNull(result);
            assertEquals(content, result.getContent());
            assertEquals(author, result.getAuthor());
            assertNotNull(result.getCreatedAt());

            verify(postRepository).save(any(Post.class));
        }

        @Test
        @DisplayName("Should throw exception if author does not exist")
        void shouldThrowWhenAuthorMissing() {
            when(userRepository.findById(author.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> postService.createPost(author.getId(), "Content"));

            verify(postRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Reply Post Tests")
    class ReplyPostTests {

        @Test
        @DisplayName("Should create a reply to an existing post")
        void shouldCreateReply() {
            Post parentPost = new Post();
            parentPost.setId(UUID.randomUUID());
            parentPost.setAuthor(author);

            when(userRepository.findById(author.getId()))
                    .thenReturn(Optional.of(author));

            when(postRepository.findById(parentPost.getId()))
                    .thenReturn(Optional.of(parentPost));

            when(postRepository.save(any(Post.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Post reply = postService.replyToPost(author.getId(), parentPost.getId(), "Reply");

            assertNotNull(reply);
            assertEquals("Reply", reply.getContent());
            assertEquals(parentPost, reply.getReplyTo());
            assertEquals(author, reply.getAuthor());
        }

        @Test
        @DisplayName("Should throw if parent post does not exist")
        void shouldThrowIfParentPostMissing() {
            when(userRepository.findById(author.getId()))
                    .thenReturn(Optional.of(author));

            when(postRepository.findById(any()))
                    .thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> postService.replyToPost(author.getId(), UUID.randomUUID(), "Reply"));
        }
    }

    @Nested
    @DisplayName("Repost Tests")
    class RepostTests {

        @Test
        @DisplayName("Should create a repost of an existing post")
        void shouldCreateRepost() {
            Post original = new Post();
            original.setId(UUID.randomUUID());
            original.setAuthor(author);

            when(userRepository.findById(author.getId()))
                    .thenReturn(Optional.of(author));

            when(postRepository.findById(original.getId()))
                    .thenReturn(Optional.of(original));

            when(postRepository.save(any(Post.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Post repost = postService.repost(author.getId(), original.getId());

            assertNotNull(repost);
            assertEquals(original, repost.getRepostOf());
            assertEquals(author, repost.getAuthor());
            assertNull(repost.getContent());
        }
    }

    @Nested
    @DisplayName("Delete Post Tests")
    class DeletePostTests {

        @Test
        @DisplayName("Should delete post owned by user")
        void shouldDeletePost() {
            Post post = new Post();
            post.setId(UUID.randomUUID());
            post.setAuthor(author);

            when(postRepository.findById(post.getId()))
                    .thenReturn(Optional.of(post));

            assertDoesNotThrow(() ->
                    postService.deletePost(author.getId(), post.getId()));

            verify(postRepository).delete(post);
        }

        @Test
        @DisplayName("Should throw if user is not the author")
        void shouldThrowIfNotOwner() {
            User otherUser = new User("other", "Other", "");
            otherUser.setId(UUID.randomUUID());

            Post post = new Post();
            post.setId(UUID.randomUUID());
            post.setAuthor(otherUser);

            when(postRepository.findById(post.getId()))
                    .thenReturn(Optional.of(post));

            assertThrows(IllegalStateException.class,
                    () -> postService.deletePost(author.getId(), post.getId()));
        }
    }
}
