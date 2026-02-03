package com.example.Traveler.service;

import com.example.Traveler.domain.User;
import com.example.Traveler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    // 회원가입
    public User signup(User user) {
    // 1. 중복 이메일 확인
    userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
        throw new RuntimeException("이미 사용 중인 이메일입니다.");
    });

    // 2. 저장
    return userRepository.save(user);
}

    // 로그인
    public User login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(u -> u.getPassword().equals(password))
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 틀렸습니다."));
    }
}
