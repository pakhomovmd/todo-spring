package ru.ssau.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.ssau.todo.dto.AuthRequest;
import ru.ssau.todo.dto.AuthResponse;
import ru.ssau.todo.dto.UserDto;
import ru.ssau.todo.entity.Role;  // ← ДОБАВИТЬ ЭТОТ ИМПОРТ!
import ru.ssau.todo.entity.User;
import ru.ssau.todo.repository.UserRepository;
import ru.ssau.todo.service.TokenService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          TokenService tokenService,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    /**
     * POST /auth/login
     * Логин, возвращает Access Token и Refresh Token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // Аутентификация — сохраняем результат в переменную!
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Получаем пользователя из БД
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Получаем роли из authentication (теперь переменная определена!)
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(role -> role.startsWith("ROLE_"))
                .collect(Collectors.toList());

        // Создаём токены (передаём username)
        String accessToken = tokenService.createAccessToken(user.getId(), user.getUsername(), roles);
        String refreshToken = tokenService.createRefreshToken(user.getId());

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    /**
     * POST /auth/refresh
     * Обновление Access Token по Refresh Token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            var payload = tokenService.verifyToken(refreshToken);
            Long userId = ((Number) payload.get("userId")).longValue();

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Теперь Role импортирован, ошибки нет
            List<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());

            // Создаём новый access token с username
            String newAccessToken = tokenService.createAccessToken(userId, user.getUsername(), roles);

            return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));

        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * GET /auth/me
     * Возвращает текущего пользователя (по токену из SecurityContext)
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        // Получаем username из authentication
        String username = authentication.getName();

        // Ищем пользователя в БД, чтобы получить ID
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());  // ← УБЕДИСЬ, ЧТО ЭТО ЕСТЬ!
        userDto.setUsername(user.getUsername());
        userDto.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(userDto);
    }
}