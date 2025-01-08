package com.dyalex.urlshortener.service;

import com.dyalex.urlshortener.model.User;
import com.dyalex.urlshortener.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.dyalex.urlshortener.service.UrlService.getUserUuid;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User getOrCreateUser(UUID uuid) {
        Optional<User> userOpt = userRepository.findByUuid(uuid);
        return userOpt.orElseGet(() -> {
            User newUser = User.builder()
                    .uuid(UUID.randomUUID())
                    .build();
            return userRepository.save(newUser);
        });
    }

    public Optional<User> getUserByUuid(String uuid) {
        return userRepository.findByUuid(getUserUuid(uuid));
    }
}
