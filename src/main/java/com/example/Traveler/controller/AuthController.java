package com.example.Traveler.controller;

import com.example.Traveler.domain.User;
import com.example.Traveler.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public String signup(@RequestBody User user) {
        authService.signup(user);
        return "회원가입 성공!";
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        User loggedInUser = authService.login(user.getEmail(), user.getPassword());
        return loggedInUser.getNickname() + "님, 환영합니다!";
    }
}
