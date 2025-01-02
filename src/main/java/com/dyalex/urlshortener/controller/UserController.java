package com.dyalex.urlshortener.controller;

import com.dyalex.urlshortener.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

@AllArgsConstructor
@Controller
public class UserController {
    private final UserService userService;
}
