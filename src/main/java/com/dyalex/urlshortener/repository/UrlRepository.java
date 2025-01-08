package com.dyalex.urlshortener.repository;

import com.dyalex.urlshortener.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortUrl(String shortUrl);

    void deleteById(Long id);

    List<Url> findAllByUserUuid(UUID uuid);
}
