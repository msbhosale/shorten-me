package com.msbhosale.tiny.controller;

import com.msbhosale.tiny.dto.ShortUrlRequest;
import com.msbhosale.tiny.dto.ShortUrlResponse;
import com.msbhosale.tiny.service.ShortUrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AppController {

    private final ShortUrlService shortUrlService;

    @PostMapping("/urls")
    public ResponseEntity<ShortUrlResponse> saveLongUrl(@RequestBody @Valid ShortUrlRequest urlRequest) {

        ShortUrlResponse shortUrlResponse = shortUrlService.saveUrl(urlRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(shortUrlResponse);
    }

    @GetMapping("/urls/{shortCode}")
    public ResponseEntity<ShortUrlResponse> getLongUrl(@PathVariable String shortCode) {

        ShortUrlResponse shortUrlResponse = shortUrlService.getShortUrlResponse(shortCode);

        return ResponseEntity.status(HttpStatus.OK).body(shortUrlResponse);
    }

    @GetMapping("/urls")
    public ResponseEntity<List<ShortUrlResponse>> getLongUrlForUser(@RequestParam Long userId) {

        List<ShortUrlResponse> urlResponses = shortUrlService.getShortUrlsForUser(userId);

        return ResponseEntity.ok(urlResponses);
    }

}
