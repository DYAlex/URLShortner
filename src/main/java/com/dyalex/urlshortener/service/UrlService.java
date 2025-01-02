package com.dyalex.urlshortener.service;

import com.dyalex.urlshortener.repository.UrlRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UrlService {
    private UrlRepository urlRepository;
}
