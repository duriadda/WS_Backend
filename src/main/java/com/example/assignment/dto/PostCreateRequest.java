package com.example.assignment.dto;

import com.example.assignment.common.exception.ApiException;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 작성(POST) 요청 본문.
 */
@Getter
@NoArgsConstructor
public class PostCreateRequest {

    private Long authorId;
    private String title;
    private String content;

    public void validate() {
        if (authorId == null) {
            throw ApiException.badRequest("authorId 는 필수입니다.");
        }
        if (title == null || title.isBlank()) {
            throw ApiException.badRequest("title 은 필수입니다.");
        }
        if (title.length() > 100) {
            throw ApiException.badRequest("title 은 100자 이하여야 합니다.");
        }
        if (content == null || content.isBlank()) {
            throw ApiException.badRequest("content 는 필수입니다.");
        }
    }
}
