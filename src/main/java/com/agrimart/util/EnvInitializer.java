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
        System.out.println("🚀 Initializing Environment Configuration...");

        Path envPath = Paths.get(ENV_FILE);
        Path examplePath = Paths.get(EXAMPLE_FILE);

        // 1. Auto-create .env if missing
        if (!Files.exists(envPath)) {
            System.out.println("⚠️  .env file missing. Creating from .env.example...");
            if (!Files.exists(examplePath)) {
                throw new RuntimeException(
                        "CRITICAL ERROR: .env.example missing! Please ensure it exists in the root directory.");
            }
            try {
                Files.copy(examplePath, envPath, StandardCopyOption.COPY_ATTRIBUTES);
                System.out.println("✅ .env file created successfully.");
            } catch (IOException e) {
                throw new RuntimeException("CRITICAL ERROR: Failed to copy .env.example to .env", e);
            }
        }

        // 2. Fail-fast validation
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
