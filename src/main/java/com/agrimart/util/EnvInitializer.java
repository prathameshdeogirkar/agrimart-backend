package com.agrimart.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnvInitializer {

    private static final String ENV_FILE = ".env";
    private static final String EXAMPLE_FILE = ".env.example";


    public static void init() {

    // ☁️ Detect Railway / cloud environment
    if (System.getenv("RAILWAY_ENVIRONMENT") != null
            || System.getenv("RAILWAY_PROJECT_ID") != null
            || System.getenv("PORT") != null) {

        System.out.println("☁️ Cloud environment detected — skipping .env initialization");
        return;
    }

    System.out.println("🚀 Initializing local environment configuration...");

    Path envPath = Paths.get(".env");
    Path examplePath = Paths.get(".env.example");

    if (!Files.exists(envPath)) {
        try {
            Files.copy(examplePath, envPath);
            System.out.println("✅ .env file created from .env.example");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create .env file", e);
        }
    }

    validateEnv();
}



    private static void validateEnv() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(ENV_FILE));
            Map<String, String> envVars = lines.stream()
                    .filter(line -> line != null && !line.trim().isEmpty() && !line.startsWith("#")
                            && line.contains("="))
                    .map(line -> line.split("=", 2))
                    .collect(Collectors.toMap(
                            parts -> parts[0].trim(),
                            parts -> parts.length > 1 ? parts[1].trim() : ""));

            checkPlaceholder(envVars, "DB_PASSWORD", "your_password_here");
            checkPlaceholder(envVars, "RAZORPAY_KEY_ID", "your_razorpay_key_id");
            checkPlaceholder(envVars, "RAZORPAY_KEY_SECRET", "your_razorpay_key_secret");
            checkPlaceholder(envVars, "MAIL_USERNAME", "your_email@gmail.com");
            checkPlaceholder(envVars, "MAIL_PASSWORD", "your_app_password");

            System.out.println("✅ Environment configuration validated.");

        } catch (IOException e) {
            throw new RuntimeException("CRITICAL ERROR: Failed to read .env file for validation", e);
        }
    }

    private static void checkPlaceholder(Map<String, String> envVars, String key, String placeholder) {
        String value = envVars.get(key);
        if (value == null || value.isEmpty() || value.equalsIgnoreCase(placeholder)) {
            System.err.println("\n❌ CONFIGURATION ERROR: " + key + " is not set in .env!");
            System.err.println(
                    "👉 Please open the .env file and replace '" + placeholder + "' with your actual credentials.");

            // Hard exit to prevent application from running with broken configs
            System.exit(1);
        }
    }
}
