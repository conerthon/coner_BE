package com.example.Traveler.controller;

import com.example.Traveler.domain.TravelGroup;
import com.example.Traveler.domain.User;
import com.example.Traveler.dto.GroupRequest;
import com.example.Traveler.dto.GroupResponse;
import com.example.Traveler.repository.UserRepository; // 테스트
import com.example.Traveler.service.GroupService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final UserRepository userRepository; // 테스트


    // 그룹 생성
    @PostMapping
    public ResponseEntity<?> createGroup(
            @RequestBody GroupRequest.Create request,
            HttpSession session) {

        // 테스트를 위해 유저 1번(호스트)을 세션에 넣음. 추후에 수정 필요
        User testUser = userRepository.findById(1L).orElse(null);
        session.setAttribute("loginUser", testUser);
        
        // 1. 세션에서 로그인 유저 가져오기
        User loginUser = (User) session.getAttribute("loginUser");

        // 2. 로그인 여부 체크
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }

        // 3. 그룹 생성 실행
        TravelGroup group = groupService.createGroup(request.getGroupName(), loginUser);

        // 4. 응답 DTO 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(new GroupResponse(group));
    }

    // 그룹 초대(가입)
    @PostMapping("/join")
    public ResponseEntity<String> joinGroup(
            @RequestBody GroupRequest.Join request,
            HttpSession session) {

        // 테스트를 위해 유저 2번(멤버)을 세션에 넣음. 추후에 수정 필요
        User testUser = userRepository.findById(2L).orElse(null);
        session.setAttribute("loginUser", testUser);

        // 1. 세션에서 로그인 유저 가져오기
        User loginUser = (User) session.getAttribute("loginUser");

        // 2. 로그인 여부 체크
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }

        // 3. 그룹 가입 실행
        groupService.joinGroup(request.getInviteCode(), loginUser);

        return ResponseEntity.ok("성공적으로 그룹에 참여했습니다.");
    }
}