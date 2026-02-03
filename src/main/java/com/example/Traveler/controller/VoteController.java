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

    // 일정표 다운
    @GetMapping("/confirmed/download")
    public void downloadItinerary(@PathVariable Long groupId, HttpServletResponse response) throws IOException {
        // 살아남은 장소 리스트 가져오기
        List<ConfirmedPlaceResponse> confirmedPlaces = voteService.getConfirmedPlaces(groupId);

        // 엑셀 시트 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("여행 일정표");

        // 헤더 행
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("순번");
        headerRow.createCell(1).setCellValue("장소명");
        headerRow.createCell(2).setCellValue("키워드");

        // 데이터 행
        int rowNum = 1;
        for (ConfirmedPlaceResponse place : confirmedPlaces) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1); // 1, 2, 3...
            row.createCell(1).setCellValue(place.getTitle());
            row.createCell(2).setCellValue(place.getKeyword());
        }


        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

        // 파일 다운로드를 위한 응답 설정
        String fileName = "travel_itinerary.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        // 파일 전송하고 종료
        workbook.write(response.getOutputStream());
        workbook.close();
    }


    @Getter
    @NoArgsConstructor
    public static class VoteRequest {
        @JsonProperty("isLike")
        private boolean isLike;
    }
}