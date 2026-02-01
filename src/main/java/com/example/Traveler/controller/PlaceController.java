package com.example.Traveler.controller;

import com.example.Traveler.domain.Place;
import com.example.Traveler.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/test-capture")
    public ResponseEntity<Place> testCapture(
            @RequestParam String url,
            @RequestParam Long userId) {

        Place result = placeService.captureUrl(url, userId);
        return ResponseEntity.ok(result);
    }
}
