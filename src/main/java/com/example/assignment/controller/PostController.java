package com.example.assignment.controller;

import com.example.assignment.common.ApiResponse;
import com.example.assignment.domain.Post;
import com.example.assignment.dto.PageResponse;
import com.example.assignment.dto.PostCreateRequest;
import com.example.assignment.dto.PostUpdateRequest;
import com.example.assignment.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 수정·삭제 시에는 X-User-Id 헤더로 요청자를 식별한다. (로그인 기능 대신 사용)
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // [POST 2] 게시글 작성 → 201
    @PostMapping
    public ResponseEntity<ApiResponse<Post>> create(@RequestBody PostCreateRequest request) {
        Post post = postService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(post));
    }

    // [GET 2] 게시글 목록 조회 (페이지네이션, 작성자 필터) → 200
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Post>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long authorId) {
        return ResponseEntity.ok(ApiResponse.success(postService.list(page, size, authorId)));
    }

    // [PUT 2] 게시글 수정 → 200
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Post>> update(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long requesterId,
            @RequestBody PostUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(postService.update(id, requesterId, request)));
    }

    // [DELETE 2] 게시글 삭제 → 200
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long requesterId) {
        postService.delete(id, requesterId);
        return ResponseEntity.ok(ApiResponse.success(null, "게시글이 삭제되었습니다."));
    }
}
