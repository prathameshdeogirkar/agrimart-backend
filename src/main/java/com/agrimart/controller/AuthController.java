package com.agrimart.controller;

import com.agrimart.dto.LoginRequest;
import com.agrimart.dto.RegisterRequest;
import com.agrimart.entity.User;
import com.agrimart.service.AuthService;
import com.agrimart.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // ✅ REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        User user = authService.register(
                request.getName(),
                request.getEmail(),
                request.getPassword());

        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "email", user.getEmail()));
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = authService.login(
                request.getEmail(),
                request.getPassword());

        // ✅ FIXED: Pass role to JWT generator
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return ResponseEntity.ok(Map.of("token", token));
    }
}
