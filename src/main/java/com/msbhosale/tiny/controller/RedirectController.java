package com.msbhosale.tiny.controller;

import com.msbhosale.tiny.entity.ShortUrl;
import com.msbhosale.tiny.service.ShortUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URI;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class RedirectController {

    private final ShortUrlService shortUrlService;

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {

        Optional<ShortUrl> shortUrlOptional = shortUrlService.getRedirectUrl(shortCode);

        return shortUrlOptional.<ResponseEntity<Void>>map(shortUrl -> ResponseEntity
                        .status(HttpStatus.TEMPORARY_REDIRECT)
                        .location(URI.create(shortUrl.getOriginalUrl()))
                        .build())
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }
}
