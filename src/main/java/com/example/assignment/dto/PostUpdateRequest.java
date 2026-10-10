package com.example.assignment.dto;

import com.example.assignment.common.exception.ApiException;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 수정(PUT) 요청 본문. 작성자는 바꿀 수 없으므로 authorId 는 받지 않는다.
 */
@Getter
@NoArgsConstructor
public class PostUpdateRequest {

    private String title;
    private String content;

    public void validate() {
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
