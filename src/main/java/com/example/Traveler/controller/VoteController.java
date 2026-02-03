package com.example.Traveler.controller;

import com.example.Traveler.domain.User;
import com.example.Traveler.dto.ConfirmedPlaceResponse;
import com.example.Traveler.dto.VoteResultResponse;
import com.example.Traveler.repository.UserRepository;
import com.example.Traveler.service.VoteService;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/places")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;
    private final UserRepository userRepository; // 테스트용 세션 주입을 위해 잠시 추가

    // 장소 투표
    @PostMapping("/{placeId}/vote")
    public ResponseEntity<?> vote(
            @PathVariable Long groupId,
            @PathVariable Long placeId,
            @RequestBody VoteRequest request,
            HttpSession session) {

        // 테스트용 세션 -> 로그인 로직 합치면 삭제
        if (session.getAttribute("loginUser") == null) {
            userRepository.findById(1L).ifPresent(u -> session.setAttribute("loginUser", u));
        }
        
        // 로그인 되었는지 확인
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            voteService.castVote(groupId, placeId, loginUser.getId(), request.isLike());
            return ResponseEntity.ok("투표가 성공적으로 완료되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    // 투표 결과 조회
    @GetMapping("/{placeId}/vote/results")
    public ResponseEntity<VoteResultResponse> getVoteResult(
            @PathVariable Long groupId,
            @PathVariable Long placeId) {

        return ResponseEntity.ok(voteService.getVoteResult(groupId, placeId));
    }

    // 일정에 들어간 장소 리스트 조회
    @GetMapping("/confirmed")
    public ResponseEntity<List<ConfirmedPlaceResponse>> getConfirmedPlaces(
            @PathVariable Long groupId) {

        return ResponseEntity.ok(voteService.getConfirmedPlaces(groupId));
    }

    // 살아남은 장소 개수만 반환
    @GetMapping("/confirmed/count")
    public ResponseEntity<Integer> getConfirmedCount(@PathVariable Long groupId) {
        return ResponseEntity.ok(voteService.getConfirmedCount(groupId));
    }


    @Getter
    @NoArgsConstructor
    public static class VoteRequest {
        @JsonProperty("isLike")
        private boolean isLike;
    }
}