package com.msbhosale.tiny.service;

import com.msbhosale.tiny.config.AppProperties;
import com.msbhosale.tiny.dto.ShortUrlResponse;
import com.msbhosale.tiny.entity.ShortUrl;
import com.msbhosale.tiny.exception.UrlNotFoundException;
import com.msbhosale.tiny.repository.ShortUrlRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ShortUrlServiceTest {

    @Mock
    ShortUrlRepository shortUrlRepository;

    @Mock
    AppProperties appProperties;

    @InjectMocks
    ShortUrlService shortUrlService;

    @Test
    void getShortUrlResponse_shouldReturnResponse_whenShortCodeExists() {

        ShortUrl shortUrl = ShortUrl.builder()
                .shortCode("abc123")
                .originalUrl("https://example.com")
                .createdAt(LocalDateTime.now())
                .expiryAt(LocalDateTime.now().plusDays(365))
                .build();

        Mockito.when(shortUrlRepository.findByShortCode("abc123"))
                .thenReturn(Optional.of(shortUrl));
        Mockito.when(appProperties.getHost())
                .thenReturn("http://localhost:8085");

        ShortUrlResponse response = shortUrlService.getShortUrlResponse("abc123");

        assertEquals("abc123", response.getShortCode());
        assertEquals("https://example.com", response.getOriginalUrl());
        assertEquals("http://localhost:8085/abc123", response.getShortenedUrl());

        verify(shortUrlRepository).findByShortCode("abc123");
    }

    @Test
    void getShortUrlResponse_shouldThrowException_whenShortCodeDoesNotExist() {

        Mockito.when(shortUrlRepository.findByShortCode("abc123"))
                .thenReturn(Optional.empty());

        UrlNotFoundException exception = assertThrows(
                UrlNotFoundException.class,
                () -> shortUrlService.getShortUrlResponse("abc123")
        );

        assertEquals("Short url not found for the code abc123", exception.getMessage());

        Mockito.verify(shortUrlRepository).findByShortCode("abc123");
    }
}