package com.dyalex.urlshortener.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "urls")
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String fullUrl;

    private String shortUrl;

    @JsonFormat(pattern = ("yyyy/MM/dd HH:mm:ss"))
    private LocalDateTime createdAt;

    private Long ttl;

    private Long maxFollows;

    private Long followCounter;
}
