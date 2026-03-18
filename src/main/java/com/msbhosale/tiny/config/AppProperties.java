package com.msbhosale.tiny.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.short-url")
@Getter
@Setter
public class AppProperties {
    private String baseUrl;
}