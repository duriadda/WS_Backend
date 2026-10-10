package com.example.assignment.service;

import com.example.assignment.common.exception.ApiException;
import com.example.assignment.domain.Post;
import com.example.assignment.dto.PageResponse;
import com.example.assignment.dto.PostCreateRequest;
import com.example.assignment.dto.PostUpdateRequest;
import com.example.assignment.repository.PostRepository;
import com.example.assignment.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final int MAX_PAGE_SIZE = 100;

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Post create(PostCreateRequest request) {
        request.validate();
        // 존재하지 않는 작성자 → 404
        userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> ApiException.notFound("id=" + request.getAuthorId() + " 사용자를 찾을 수 없습니다."));
        return postRepository.save(request.getAuthorId(), request.getTitle(), request.getContent());
    }

    public PageResponse<Post> list(int page, int size, Long authorId) {
        // 잘못된 페이지 파라미터 → 400
        if (page < 0) {
            throw ApiException.badRequest("page 는 0 이상이어야 합니다.");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw ApiException.badRequest("size 는 1 이상 " + MAX_PAGE_SIZE + " 이하여야 합니다.");
        }
        List<Post> all = postRepository.findAll(authorId);
        List<Post> content = all.stream()
                .skip((long) page * size)
                .limit(size)
                .toList();
        return new PageResponse<>(content, page, size, all.size());
    }

    public Post update(Long id, Long requesterId, PostUpdateRequest request) {
        Post post = getOwnedPost(id, requesterId);
        request.validate();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        return post;
    }

    public void delete(Long id, Long requesterId) {
        getOwnedPost(id, requesterId);
        postRepository.deleteById(id);
    }

    // 게시글이 없으면 404, 요청자가 작성자가 아니면 403
    private Post getOwnedPost(Long id, Long requesterId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("id=" + id + " 게시글을 찾을 수 없습니다."));
        if (!post.getAuthorId().equals(requesterId)) {
            throw ApiException.forbidden("본인이 작성한 게시글만 수정·삭제할 수 있습니다.");
        }
        return post;
    }
}
