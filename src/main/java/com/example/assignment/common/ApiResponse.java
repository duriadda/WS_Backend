package com.example.assignment.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 모든 API 가 공통으로 사용하는 표준 응답 포맷.
 * 성공: { "status": "success", "data": { ... } }
 * 실패: { "status": "error", "message": "..." }
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"status", "data", "message"})
public class ApiResponse<T> {

    private final String status;
    private final T data;
    private final String message;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", data, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>("success", data, message);
    }

    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>("error", null, message);
    }
}
