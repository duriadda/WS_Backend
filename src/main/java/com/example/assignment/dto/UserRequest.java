package com.example.assignment.dto;

import com.example.assignment.common.exception.ApiException;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 생성(POST)·수정(PUT) 요청 본문.
 * Validation 라이브러리를 쓰지 않으므로 validate() 로 직접 검증한다.
 */
@Getter
@NoArgsConstructor
public class UserRequest {

    private String name;
    private String email;

    public void validate() {
        if (name == null || name.isBlank()) {
            throw ApiException.badRequest("name 은 필수입니다.");
        }
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw ApiException.badRequest("email 형식이 올바르지 않습니다.");
        }
    }
}
