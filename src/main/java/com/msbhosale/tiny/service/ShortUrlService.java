package com.msbhosale.tiny.service;

import com.msbhosale.tiny.config.AppProperties;
import com.msbhosale.tiny.dto.ShortUrlRequest;
import com.msbhosale.tiny.dto.ShortUrlResponse;
import com.msbhosale.tiny.entity.ShortUrl;
import com.msbhosale.tiny.exception.UrlExpiredException;
import com.msbhosale.tiny.exception.UrlNotFoundException;
import com.msbhosale.tiny.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ShortUrlRepository shortUrlRepository;
    private final AppProperties appProperties;

    public ShortUrlResponse saveUrl(ShortUrlRequest urlRequest) {

        String hash = getHash(urlRequest.getOriginalUrl(), urlRequest.getUserId());

        Optional<ShortUrl> urlOptional = shortUrlRepository.findByUrlHashAndUserId(hash, urlRequest.getUserId());

        if (urlOptional.isPresent()) {
            return mapToResponse(urlOptional.get());
        }

        ShortUrl shortUrlEntity = ShortUrl.builder()
                .originalUrl(urlRequest.getOriginalUrl())
                .userId(urlRequest.getUserId())
                .urlHash(hash)
                .build();

        ShortUrl saved = generateShortCodeAndSave(shortUrlEntity);

        return mapToResponse(saved);
    }

    @Cacheable(value = "urlCache", key = "#shortCode")
    public ShortUrlResponse getShortUrlResponse(String shortCode) {

        log.info("Getting data from DB for the shortCode {}", shortCode);

        if (shortCode.isBlank()) {
            throw new IllegalArgumentException("Short code must not be null or blank");
        }

        ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(
                        String.format("Short url not found for the code %s", shortCode)));

        if (shortUrl.isExpired()) {
            log.info("{} is expired", shortUrl.getShortCode());
            throw new UrlExpiredException("ShortUrl has expired");
        }

        return mapToResponse(shortUrl);
    }

    @Cacheable(value = "redirectCache", key = "#shortCode")
    public ShortUrlResponse getRedirectUrl(String shortCode) {

        ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(
                        String.format("Short url not found for the code %s", shortCode)));

        if (shortUrl.isExpired()) {
            log.info("This url with code {} is expired", shortUrl.getShortCode());
            throw new UrlExpiredException("ShortUrl has expired");
        }

        return mapToResponse(shortUrl);
    }

    public List<ShortUrlResponse> getShortUrlsForUser(Long userId) {

        List<ShortUrl> shortUrls = shortUrlRepository.findByUserId(userId);

        return shortUrls.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private String getHash(String originalUrl, Long userId) {
        return DigestUtils.sha256Hex(originalUrl + userId);
    }

    private ShortUrl generateShortCodeAndSave(ShortUrl shortUrlEntity) {

        for (int attempt = 1; true; attempt++) {

            String shortCode = generateShortCode();
            shortUrlEntity.setShortCode(shortCode);

            try {
                return shortUrlRepository.save(shortUrlEntity);
            } catch (DataIntegrityViolationException ex) {
                if (attempt == 3) {
                    throw new RuntimeException("Failed to generate unique short code after 3 attempts");
                }
            }
        }
    }

    private ShortUrlResponse mapToResponse(ShortUrl shortUrl) {
        return ShortUrlResponse.builder()
                .shortCode(shortUrl.getShortCode())
                .originalUrl(shortUrl.getOriginalUrl())
                .shortenedUrl(appProperties.getHost() + "/" + shortUrl.getShortCode())
                .createdAt(shortUrl.getCreatedAt())
                .expiryAt(shortUrl.getExpiryAt())
                .self(appProperties.getHost() + "/" + appProperties.getBasePath() + "/urls/" + shortUrl.getShortCode())
                .build();
    }

    private String generateShortCode() {

        StringBuilder sb = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            sb.append(BASE62.charAt(RANDOM.nextInt(BASE62.length())));
        }

        return sb.toString();
    }
}
