package ru.ssau.todo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TokenService {

    private final String secret;
    private final ObjectMapper objectMapper;

    public TokenService() {
        this.secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET environment variable not set or too short (need 32+ chars)");
        }
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Генерация токена из payload
     */
    public String generateToken(Map<String, Object> payload) {
        try {
            // Шаг 1: Преобразование payload в JSON
            String jsonPayload = objectMapper.writeValueAsString(payload);

            // Шаг 2: Кодирование payload в Base64 URL
            String encodedPayload = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(jsonPayload.getBytes());

            // Шаг 3: Создание подписи (HmacSHA256)
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            mac.init(keySpec);
            byte[] signatureBytes = mac.doFinal(encodedPayload.getBytes());

            // Шаг 4: Кодирование подписи
            String encodedSignature = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(signatureBytes);

            // Шаг 5: Сборка токена
            return encodedPayload + "." + encodedSignature;

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    /**
     * Проверка токена и получение payload
     */
    public Map<String, Object> verifyToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 2) {
                throw new RuntimeException("Invalid token format: expected 2 parts, got " + parts.length);
            }

            String encodedPayload = parts[0];
            String encodedSignature = parts[1];

            // Проверка подписи
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            mac.init(keySpec);
            byte[] expectedSignature = mac.doFinal(encodedPayload.getBytes());
            String expectedEncodedSignature = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(expectedSignature);

            if (!expectedEncodedSignature.equals(encodedSignature)) {
                throw new RuntimeException("Invalid signature");
            }

            // Декодирование payload
            byte[] decodedPayload = Base64.getUrlDecoder().decode(encodedPayload);
            String jsonPayload = new String(decodedPayload);

            @SuppressWarnings("unchecked")
            Map<String, Object> payload = objectMapper.readValue(jsonPayload, Map.class);

            // Проверка срока действия
            Long exp = ((Number) payload.get("exp")).longValue();
            long now = System.currentTimeMillis() / 1000;

            if (exp < now) {
                throw new RuntimeException("Token expired");
            }

            return payload;

        } catch (Exception e) {
            throw new RuntimeException("Token verification failed: " + e.getMessage(), e);
        }
    }

    /**
     * Создание Access Token (15 минут)
     */
    public String createAccessToken(Long userId, String username, List<String> roles) {
        long now = System.currentTimeMillis() / 1000;
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("username", username);  // ← добавляем username
        payload.put("roles", roles);
        payload.put("iat", now);
        payload.put("exp", now + 15 * 60);
        return generateToken(payload);
    }

    /**
     * Создание Refresh Token (7 дней)
     */
    public String createRefreshToken(Long userId) {
        long now = System.currentTimeMillis() / 1000;
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("iat", now);
        payload.put("exp", now + 7 * 24 * 60 * 60);  // +7 дней
        return generateToken(payload);
    }

    /**
     * Получение userId из токена (без проверки срока действия)
     */
    public Long getUserIdFromToken(String token) {
        Map<String, Object> payload = verifyToken(token);
        return ((Number) payload.get("userId")).longValue();
    }

    /**
     * Получение ролей из токена
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Map<String, Object> payload = verifyToken(token);
        return (List<String>) payload.get("roles");
    }
}