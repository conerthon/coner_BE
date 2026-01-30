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
        // 실제로는 여기서 비밀번호 암호화(BCrypt)를 해야 하지만, 일단 평문으로 진행합니다.
        return userRepository.save(user);
    }

    // 로그인
    public User login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(u -> u.getPassword().equals(password))
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 틀렸습니다."));
    }
}
