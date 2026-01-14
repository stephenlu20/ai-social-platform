package com.aisocial.platform.repository;

import com.aisocial.platform.entity.Post;
import com.aisocial.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findByAuthor(User author);

    List<Post> findByReplyTo(Post replyTo);

    List<Post> findByRepostOf(Post repostOf);

    List<Post> findByReplyTo_IdOrderByCreatedAtAsc(UUID parentPostId);

    @Query("""
        SELECT p
        FROM Post p
        WHERE p.author IN :authors
        ORDER BY p.createdAt DESC, p.id DESC
    """)
    List<Post> findFeedPostsByAuthors(@Param("authors") List<User> authors);
}
