package com.company.ems.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token service — generates and validates Bearer tokens after login.
 * Tokens are stored in-memory (suitable for learning; use JWT + Redis in production).
 */
@Service
public class TokenService {

    private final Map<String, TokenDetails> activeTokens = new ConcurrentHashMap<>();

    public String generateToken(String username, String role) {
        String token = UUID.randomUUID().toString();
        activeTokens.put(token, new TokenDetails(username, role));
        return token;
    }

    public boolean isValid(String token) {
        return token != null && activeTokens.containsKey(token);
    }

    public TokenDetails getDetails(String token) {
        return activeTokens.get(token);
    }

    public record TokenDetails(String username, String role) {}
}
