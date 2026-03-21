package com.msbhosale.tiny.repository;

import com.msbhosale.tiny.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    Optional<ShortUrl> findByUrlHashAndUserId(String hash, Long userId);

    Optional<ShortUrl> findByShortCode(String shortCode);

    List<ShortUrl> findByUserId(Long userId);
}
