package com.dyalex.urlshortener.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class UrlDto {

    private String fullUrl;

    private String shortUrl;

    @JsonFormat(pattern = ("yyyy/MM/dd HH:mm:ss"))
    private LocalDateTime createdAt;

    private Long validUntil;

    private Long maxFollows;

    private Long followCounter;
}
