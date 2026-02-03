package com.example.Traveler.controller;

import com.example.Traveler.domain.User;
import com.example.Traveler.dto.LoginRequest;
import com.example.Traveler.dto.UserResponse;
import com.example.Traveler.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 1. 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            User savedUser = authService.signup(user);
            // 가입 후 바로 유저 정보를 반환해서 프론트가 활용하게 함
            return ResponseEntity.ok(new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getNickname(),
                savedUser.getProfileImage()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        try {
            User user = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            session.setAttribute("loginUser", user);

            return ResponseEntity.ok(new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImage()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // 3. 로그인 상태 체크
    @GetMapping("/check")
    public ResponseEntity<?> checkStatus(HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return ResponseEntity.status(401).body("unauthorized");

        return ResponseEntity.ok(new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getNickname(),
            user.getProfileImage()
        ));
    }

    // 4. 로그아웃
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 날리기
        return "로그아웃 되었습니다.";
    }
}
