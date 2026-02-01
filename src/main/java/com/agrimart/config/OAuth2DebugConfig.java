package com.agrimart.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth2DebugConfig {

    @Value("${spring.security.oauth2.client.registration.google.client-id:NOT_FOUND}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret:NOT_FOUND}")
    private String clientSecret;

    @PostConstruct
    public void debugCredentials() {
        System.out.println("================= OAUTH2 CREDENTIAL DEBUG ==================");
        logValue("CLIENT_ID", clientId);
        logValue("CLIENT_SECRET", clientSecret);
        System.out.println("============================================================");
    }

    private void logValue(String name, String value) {
        if ("NOT_FOUND".equals(value) || value == null) {
            System.err.println("❌ " + name + " is MISSING or Defaulted!");
            return;
        }

        int len = value.length();
        String start = len > 3 ? value.substring(0, 3) : value;
        String end = len > 3 ? value.substring(len - 3) : "";

        System.out.println("✅ Loaded " + name + ":");
        System.out.println("   - Length: " + len);
        System.out.println("   - Starts with: '" + start + "'");
        System.out.println("   - Ends with:   '" + end + "'");

        // Critical check for hidden whitespace
        if (!value.trim().equals(value)) {
            System.err.println("⚠️ WARNING: " + name + " contains hidden SPACES at start or end!");
        }
    }
}
