package com.msbhosale.tiny.controller;

import com.msbhosale.tiny.dto.ShortUrlRequest;
import com.msbhosale.tiny.dto.ShortUrlResponse;
import com.msbhosale.tiny.service.ShortUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AppController {

    private final ShortUrlService shortUrlService;

    @PostMapping("/urls")
    public ResponseEntity<ShortUrlResponse> save(@RequestBody ShortUrlRequest urlRequest) {

        ShortUrlResponse shortUrlResponse = shortUrlService.saveUrl(urlRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(shortUrlResponse);
    }

    @GetMapping("/urls/{shortCode}")
    public ResponseEntity<ShortUrlResponse> save(@PathVariable String shortCode) {

        ShortUrlResponse shortUrlResponse = shortUrlService.getShortUrlResponse(shortCode);

        return ResponseEntity.status(HttpStatus.OK).body(shortUrlResponse);
    }
}
