package com.msbhosale.tiny.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "shortCode",
        "shortenedUrl",
        "originalUrl",
        "createdAt",
        "expiryAt",
        "self"
})
public class ShortUrlResponse implements Serializable {
    private String shortCode;
    private String originalUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiryAt;
    private String shortenedUrl;
    private String self;
}