package com.example.assignment.controller;

import com.example.assignment.common.ApiResponse;
import com.example.assignment.domain.User;
import com.example.assignment.dto.UserRequest;
import com.example.assignment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // [POST 1] 회원 생성 → 201
    @PostMapping
    public ResponseEntity<ApiResponse<User>> create(@RequestBody UserRequest request) {
        User user = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(user));
    }

    // [GET 1] 회원 단건 조회 → 200
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.get(id)));
    }

    // [PUT 1] 회원 정보 수정 → 200
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> update(@PathVariable Long id, @RequestBody UserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.update(id, request)));
    }

    // [DELETE 1] 회원 삭제 → 200 (표준 포맷을 유지하기 위해 204 대신 200 사용)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "회원이 삭제되었습니다."));
    }
}
