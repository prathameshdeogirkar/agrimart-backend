package com.agrimart.security.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${app.oauth2.authorized-redirect-uris}")
    private String redirectUri; // Defaults to localhost or configured prod URL

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        System.err.println("❌ OAuth2 Login Failed: " + exception.getMessage());
        exception.printStackTrace();

        // Extract base URL from the configured redirect URI (remove /oauth/callback if
        // present)
        String baseUrl = redirectUri.replace("/oauth/callback", "");
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        // Redirect to Frontend Login page with error message
        String targetUrl = baseUrl + "/login?error="
                + URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
