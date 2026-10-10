package com.example.assignment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 과제 요구 API 8개와 별개인 시연용 보조 엔드포인트.
 * 500 응답을 확인하기 위해 일부러 예외를 발생시킨다.
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/error")
    public void error() {
        throw new IllegalStateException("500 응답 시연을 위한 의도적인 예외");
    }
}
