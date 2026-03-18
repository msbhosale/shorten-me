package com.msbhosale.tiny.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ShortUrlRequest {
    private String originalUrl;
    private Long userId;
}
