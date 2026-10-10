package com.example.assignment.service;

import com.example.assignment.common.exception.ApiException;
import com.example.assignment.domain.User;
import com.example.assignment.dto.UserRequest;
import com.example.assignment.repository.PostRepository;
import com.example.assignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public User create(UserRequest request) {
        request.validate();
        // 이메일 중복 → 409
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw ApiException.conflict("이미 사용 중인 이메일입니다.");
        });
        return userRepository.save(request.getName(), request.getEmail());
    }

    public User get(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("id=" + id + " 사용자를 찾을 수 없습니다."));
    }

    public User update(Long id, UserRequest request) {
        User user = get(id);
        request.validate();
        // 다른 사용자가 이미 쓰는 이메일로 바꾸려는 경우 → 409
        userRepository.findByEmail(request.getEmail())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw ApiException.conflict("이미 사용 중인 이메일입니다.");
                });
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return user;
    }

    public void delete(Long id) {
        get(id);
        // 회원이 삭제되면 그 회원의 게시글도 함께 삭제
        postRepository.deleteByAuthorId(id);
        userRepository.deleteById(id);
    }
}
