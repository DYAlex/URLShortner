package com.dyalex.urlshortener.controller;

import com.dyalex.urlshortener.model.Url;
import com.dyalex.urlshortener.model.User;
import com.dyalex.urlshortener.service.UrlService;
import com.dyalex.urlshortener.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Controller
@Slf4j
public class UserController {
    private final UrlService urlService;
    private final UserService userService;

    @GetMapping("/")
    public String index(Model model, @CookieValue(value = "uuidCookie",
            defaultValue = "Guest") String userUuid) {
        if (!userUuid.equals("Guest")) {
            model.addAttribute("user", new User());
        }
        model.addAttribute("url", new Url());
        model.addAttribute("urls", urlService.getAllUrlsByUserUuid(userUuid));
        return "index";
    }

    @GetMapping("/welcome")
    @ResponseBody
    public String welcomeUser(@CookieValue(value = "uuidCookie",
            defaultValue = "Guest") String userUuid) {
        return "Welcome, " + userUuid + "!";
    }

    @PostMapping("/setCookies")
    public String login(@RequestParam Map<String, Object> payload, HttpServletResponse response) {
        Optional<User> userOpt = userService.getUserByUuid((String) payload.get("uuid"));
        if (userOpt.isPresent()) {
            Cookie cookie = new Cookie("uuidCookie", userOpt.get().getUuid().toString());
            response.addCookie(cookie);
        }
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response, HttpServletRequest request) {
        Cookie cookie = new Cookie("uuidCookie", "");
        response.addCookie(cookie);
        HttpSession session = request.getSession();
        session.invalidate();

        return "redirect:/";
    }
}
