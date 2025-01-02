package com.dyalex.urlshortener.controller;

import com.dyalex.urlshortener.service.UrlService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

@AllArgsConstructor
@Controller
public class UrlController {
    private UrlService urlService;
}
