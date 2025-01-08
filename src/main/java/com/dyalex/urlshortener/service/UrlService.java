package com.dyalex.urlshortener.service;

import com.dyalex.urlshortener.configs.UrlConfig;
import com.dyalex.urlshortener.model.Url;
import com.dyalex.urlshortener.model.User;
import com.dyalex.urlshortener.repository.UrlRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
@Slf4j
public class UrlService {
    private final UserService userService;
    private final UrlRepository urlRepository;
    private final UrlConfig urlConfig;

    public Url shortenUrl(String fullUrl, String userUuid, Long maxFollows, Duration ttl) {
        User user = userService.getOrCreateUser(getUserUuid(userUuid));

        Url url = Url.builder()
                .fullUrl(fullUrl)
                .shortUrl(randomCode())
                .user(user)
                .createdAt(LocalDateTime.now())
                .ttl(ttl != null
                        ? ttl.compareTo(urlConfig.getTtl()) < 0
                        ? ttl.toHours()
                        : urlConfig.getTtl().toHours()
                        : urlConfig.getTtl().toHours())
                .maxFollows(maxFollows != null
                        ? maxFollows > urlConfig.getMaxFollows()
                        ? maxFollows
                        : urlConfig.getMaxFollows()
                        : urlConfig.getMaxFollows())
                .build();

        log.info("Shortening URL: " + url);
        return urlRepository.save(url);
    }

    public Optional<Url> findByShortUrl(String shortUrl) {
        Optional<Url> urlOpt = urlRepository.findByShortUrl(shortUrl);

        if (urlOpt.isPresent()) {
            Url url = urlOpt.get();
            if (url.getFollowCounter() >= url.getMaxFollows()
                    || url.getCreatedAt().plusHours(url.getTtl()).isBefore(LocalDateTime.now())) {
                url.setActive(false);
                return Optional.of(url);
            }
            url.setFollowCounter(url.getFollowCounter() + 1);
            urlRepository.save(url);
            return Optional.of(url);
        }
        return Optional.empty();
    }

    public String editShortUrl(String shortUrl, String userUuid, Long maxFollows) {
        Optional<Url> urlOpt = urlRepository.findByShortUrl(shortUrl);
        if (urlOpt.isPresent()) {
            Url url = urlOpt.get();
            UUID uuid = getUserUuid(userUuid);
            if (uuid == null || !url.getUser().getUuid().equals(uuid)) {
                return "Invalid user uuid";
            } else {
                url.setMaxFollows(maxFollows != null ? maxFollows : urlConfig.getMaxFollows());
                return urlRepository.save(url).getShortUrl();
            }
        }
        return "Url not found";
    }

    public void deleteByShortUrl(String shortUrl, String userUuid) {
        Optional<Url> urlOpt = urlRepository.findByShortUrl(shortUrl);
        if (urlOpt.isPresent()) {
            Url url = urlOpt.get();
            UUID uuid = getUserUuid(userUuid);
            if (uuid != null && url.getUser().getUuid().equals(uuid)) {
                urlRepository.deleteById(url.getId());
            }
        }
    }

    public List<Url> getAllUrlsByUserUuid(String userUuid) {
        UUID uuid = getUserUuid(userUuid);
        return uuid != null ? urlRepository.findAllByUserUuid(uuid) : new ArrayList<>();
    }

    @Scheduled(fixedRate = 3600000) // Every hour
    private void deleteExpiredUrls() {
        log.info("Deleting expired urls");
        urlRepository.findAll().forEach(url -> {
            if (url.getCreatedAt().plusHours(url.getTtl()).isBefore(LocalDateTime.now())
                    || url.getMaxFollows() <= url.getFollowCounter()) {
                urlRepository.delete(url);
            }
        });
    }

    private String randomCode() {
        UUID uuid = UUID.randomUUID();
        long lo = uuid.getLeastSignificantBits();
        long hi = uuid.getMostSignificantBits();
        lo = (lo >> (64 - 31)) ^ lo;
        hi = (hi >> (64 - 31)) ^ hi;
        String s = String.format("%010d", Math.abs(hi) + Math.abs(lo));
        return s.substring(s.length() - 10);
    }

    public static UUID getUserUuid(String userUuid) {
        UUID uuid;
        try {
            uuid = UUID.fromString(userUuid);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return null;
        }
        return uuid;
    }
}
