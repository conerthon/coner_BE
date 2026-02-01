package com.example.Traveler.controller;

import com.example.Traveler.domain.User;
import com.example.Traveler.dto.LoginRequest;
import com.example.Traveler.dto.UserResponse;
import com.example.Traveler.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
            return ResponseEntity.ok("회원가입 성공! (ID: " + savedUser.getId() + ")");
        } catch (RuntimeException e) {
            // 중복 가입 등 에러 발생 시 400 Bad Request와 에러 메시지 반환
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        try {
            User loggedInUser = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            session.setAttribute("loginUser", loggedInUser);

            UserResponse userResponse = new UserResponse(
            loggedInUser.getNickname(),
            loggedInUser.getEmail()
        );

        return ResponseEntity.ok(userResponse);
        } catch (RuntimeException e) {
            // 로그인 실패 시 401 Unauthorized 반환
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // 3. 로그인 상태 체크
    @GetMapping("/check")
    public ResponseEntity<?> checkStatus(HttpSession session) {
        User user = (User) session.getAttribute("loginUser");

        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요한 세션입니다.");
        }

        return ResponseEntity.ok(new UserResponse(user.getNickname(), user.getEmail()));
    }

    // 4. 로그아웃
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 날리기
        return "로그아웃 되었습니다.";
    }
}
