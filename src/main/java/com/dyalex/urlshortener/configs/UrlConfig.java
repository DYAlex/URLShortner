package com.dyalex.urlshortener.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "urls")
public class UrlConfig {
    private Long maxFollows = Long.MAX_VALUE;
    private Duration ttl = Duration.ofHours(24 * 365);
}
