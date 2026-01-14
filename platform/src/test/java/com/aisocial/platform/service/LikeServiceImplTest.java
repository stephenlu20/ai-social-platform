package com.aisocial.platform.service;

import com.aisocial.platform.entity.Like;
import com.aisocial.platform.entity.Post;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.LikeRepository;
import com.aisocial.platform.repository.PostRepository;
import com.aisocial.platform.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Like Service Tests")
class LikeServiceImplTest {

    private LikeRepository likeRepository;
    private UserRepository userRepository;
    private PostRepository postRepository;
    private LikeService likeService;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        likeRepository = mock(LikeRepository.class);
        userRepository = mock(UserRepository.class);
        postRepository = mock(PostRepository.class);

        likeService = new LikeServiceImpl(likeRepository, userRepository, postRepository);

        user = new User("alice", "Alice", "");
        user.setId(UUID.randomUUID());

        post = new Post(user, "Hello world");
        post.setId(UUID.randomUUID());
    }

    @Test
    void shouldLikePost() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());
        when(likeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Like like = likeService.likePost(user.getId(), post.getId());

        assertThat(like.getUser()).isEqualTo(user);
        assertThat(like.getPost()).isEqualTo(post);
        assertThat(post.getLikeCount()).isEqualTo(1);

        verify(likeRepository).save(any());
        verify(postRepository).save(post);
    }

    @Test
    void shouldUnlikePost() {
        Like like = new Like(user, post);
        post.incrementLikeCount();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(like));

        likeService.unlikePost(user.getId(), post.getId());

        verify(likeRepository).delete(like);
        assertThat(post.getLikeCount()).isEqualTo(0);
        verify(postRepository).save(post);
    }

    @Test
    void shouldCountLikes() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(likeRepository.countByPost(post)).thenReturn(5L);

        long count = likeService.countLikes(post.getId());
        assertThat(count).isEqualTo(5L);
    }
}
