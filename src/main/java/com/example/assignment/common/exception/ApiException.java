package com.example.assignment.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 응답 코드를 함께 담는 커스텀 예외의 부모 클래스.
 * GlobalExceptionHandler 가 이 예외의 status 를 그대로 응답 코드로 사용한다.
 */
@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    // 400 Bad Request
    public static ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, message);
    }

    // 403 Forbidden
    public static ApiException forbidden(String message) {
        return new ApiException(HttpStatus.FORBIDDEN, message);
    }

    // 404 Not Found
    public static ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND, message);
    }

    // 409 Conflict
    public static ApiException conflict(String message) {
        return new ApiException(HttpStatus.CONFLICT, message);
    }
}
