package com.example.assignment.repository;

import com.example.assignment.domain.Post;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * DB 대신 메모리(Map)에 게시글을 저장한다.
 */
@Repository
public class PostRepository {

    private final Map<Long, Post> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public Post save(Long authorId, String title, String content) {
        LocalDateTime now = LocalDateTime.now();
        Post post = new Post(sequence.incrementAndGet(), authorId, title, content, now, now);
        store.put(post.getId(), post);
        return post;
    }

    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    // authorId 가 null 이면 전체, 아니면 해당 작성자의 글만 id 순으로 반환
    public List<Post> findAll(Long authorId) {
        return store.values().stream()
                .filter(post -> authorId == null || post.getAuthorId().equals(authorId))
                .sorted(Comparator.comparing(Post::getId))
                .toList();
    }

    public void deleteById(Long id) {
        store.remove(id);
    }

    public void deleteByAuthorId(Long authorId) {
        store.values().removeIf(post -> post.getAuthorId().equals(authorId));
    }
}
