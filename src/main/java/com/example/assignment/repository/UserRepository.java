package com.example.assignment.repository;

import com.example.assignment.domain.User;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * DB 대신 메모리(Map)에 회원을 저장한다. 서버를 재시작하면 데이터가 초기화된다.
 */
@Repository
public class UserRepository {

    private final Map<Long, User> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public User save(String name, String email) {
        User user = new User(sequence.incrementAndGet(), name, email);
        store.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Optional<User> findByEmail(String email) {
        return store.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}
