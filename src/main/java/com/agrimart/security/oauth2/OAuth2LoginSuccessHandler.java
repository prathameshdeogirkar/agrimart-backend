package com.agrimart.security.oauth2;

import com.agrimart.entity.AuthProvider;
import com.agrimart.entity.Role;
import com.agrimart.entity.User;
import com.agrimart.repository.UserRepository;
import com.agrimart.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${app.oauth2.authorized-redirect-uris}")
    private String redirectUri; // Frontend URL from properties

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Find or Create User
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update auth provider if needed (optional logic)
            if (user.getAuthProvider() == null) {
                user.setAuthProvider(AuthProvider.GOOGLE);
                userRepository.save(user);
            }
        } else {
            // Create new user
            user = User.builder()
                    .email(email)
                    .name(name)
                    .role(Role.USER) // Default role
                    .authProvider(AuthProvider.GOOGLE)
                    .providerId(oAuth2User.getAttribute("sub"))
                    .password("") // No password for OAuth users
                    .mobile("") // Can prompt user later
                    .build();
            userRepository.save(user);
        }

        // Generate JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // Redirect to Frontend with Token
        // Frontend should handle /oauth/callback?token=... and save it
        String targetUrl = redirectUri + "/oauth/callback?token=" + token;

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
