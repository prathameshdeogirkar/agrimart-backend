package com.agrimart.controller;

import com.agrimart.dto.UserProfileDTO;
import com.agrimart.entity.User;
import com.agrimart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(UserProfileDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .build());
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileDTO> updateMyProfile(Authentication authentication,
            @RequestBody UserProfileDTO request) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update fields (Email is read-only)
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getMobile() != null && !request.getMobile().isBlank()) {
            user.setMobile(request.getMobile());
        }

        User updatedUser = userRepository.save(user);

        return ResponseEntity.ok(UserProfileDTO.builder()
                .name(updatedUser.getName())
                .email(updatedUser.getEmail())
                .mobile(updatedUser.getMobile())
                .build());
    }
}
