package com.dyalex.urlshortener.controller;

import com.dyalex.urlshortener.model.Url;
import com.dyalex.urlshortener.service.UrlService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

@AllArgsConstructor
@RequestMapping("/api/urls")
@RestController
@Slf4j
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestParam Map<String, Object> payload,
                                             @CookieValue(value = "uuidCookie", defaultValue = "Guest") String userUuid, HttpServletResponse response) {
        String fullUrl = (String) payload.get("fullUrl");
        String maxFollowsStr = (String) payload.get("maxFollows");
        Long maxFollows = maxFollowsStr != null && !maxFollowsStr.isBlank() ? Long.valueOf(maxFollowsStr) : null;
        String ttlStr = (String) payload.get("ttl");
        Duration ttl = ttlStr != null && !ttlStr.isBlank() ? Duration.parse(ttlStr) : null;

        Url url = urlService.shortenUrl(fullUrl, userUuid, maxFollows, ttl);

        Cookie cookie = new Cookie("uuidCookie", url.getUser().getUuid().toString());
        response.addCookie(cookie);

        return ResponseEntity.ok(url.getShortUrl());
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<?> redirectToFullUrl(@PathVariable String shortUrl) {
        return urlService.findByShortUrl(shortUrl)
                .map(url -> url.getActive()
                        ? ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url.getFullUrl())).build()
                        : ResponseEntity.status(HttpStatus.GONE)
                        .body("Requested link " + url.getShortUrl() + " expired!"))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{shortUrl}")
    public ResponseEntity<String> editShortUrl(@PathVariable String shortUrl, @RequestParam Map<String, Object> payload,
                                               @CookieValue(value = "uuidCookie", defaultValue = "Guest") String userUuid) {
        String maxFollowsStr = (String) payload.get("maxFollows");
        Long maxFollows = maxFollowsStr != null && !maxFollowsStr.isBlank() ? Long.valueOf(maxFollowsStr) : null;

        String editedShortUrl = urlService.editShortUrl(shortUrl, userUuid, maxFollows);
        return ResponseEntity.ok(editedShortUrl);
    }

    @DeleteMapping("/{shortUrl}")
    public ResponseEntity<String> deleteShortUrl(@PathVariable String shortUrl, @CookieValue(value = "uuidCookie",
            defaultValue = "Guest") String userUuid) {
        urlService.deleteByShortUrl(shortUrl, userUuid);
        return ResponseEntity.ok(shortUrl);
    }
}
