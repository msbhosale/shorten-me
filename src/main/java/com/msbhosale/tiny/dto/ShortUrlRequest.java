package com.msbhosale.tiny.dto;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ShortUrlRequest {
    @NotBlank(message = "Original URL must not be blank")
    @Size(max = 2048, min = 10, message = "URL length must be between 10 to 2048 characters")
    @Pattern( regexp = "^(https?://).+", message = "URL must start with http:// or https://")
    private String originalUrl;
    @NotNull(message = "User ID must not be null")
    @Positive(message = "User ID must be greater than zero")
    private Long userId;
}
