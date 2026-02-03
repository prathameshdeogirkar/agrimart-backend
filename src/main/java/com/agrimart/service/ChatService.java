package com.agrimart.service;

import com.agrimart.dto.ChatRequest;
import com.agrimart.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatService {

    @Value("${GROQ_API_KEY:}")
    private String apiKey;

    @Value("${AI_MODEL:llama-3.1-8b-instant}")
    private String apiModel;

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String SYSTEM_PROMPT = "You are Agrimart’s customer support assistant. Only answer questions related to: Products, Orders, Payments, Delivery, Returns, Accounts. If a question is unrelated, politely refuse and guide the user back to Agrimart topics. Keep responses short, friendly, and clear.";

    // Offline Q&A Map
    private static final Map<String, ChatResponse> OFFLINE_ANSWERS = new HashMap<>();

    static {
        // Delivery
        OFFLINE_ANSWERS.put("delivery", new ChatResponse(
                "Standard delivery takes 2–4 working days. Express delivery is available for select locations.",
                List.of("How can I track my order?", "Do you deliver on weekends?", "What are the shipping charges?"),
                false));
        OFFLINE_ANSWERS.put("shipping", OFFLINE_ANSWERS.get("delivery"));
        OFFLINE_ANSWERS.put("track", new ChatResponse(
                "You can track your order in the 'My Orders' section of your profile.",
                List.of("Go to My Orders", "Delivery time?", "Return policy"),
                false));

        // Payment
        OFFLINE_ANSWERS.put("payment", new ChatResponse(
                "We accept Visa, MasterCard, UPI, Net Banking, and Cash on Delivery (COD).",
                List.of("Is COD available?", "Refund process", "Secure payment?"),
                false));
        OFFLINE_ANSWERS.put("cod", new ChatResponse(
                "Yes, Cash on Delivery is available for orders up to ₹5000.",
                List.of("Payment methods", "Delivery charges"),
                false));

        // Returns & Refunds
        OFFLINE_ANSWERS.put("return", new ChatResponse(
                "You can return products within 7 days of delivery if they are damaged or incorrect.",
                List.of("How to request a return?", "When will I get my refund?", "Exchange policy"),
                false));
        OFFLINE_ANSWERS.put("refund", new ChatResponse(
                "Refunds are processed within 5-7 business days after we receive the returned item.",
                List.of("Check refund status", "Return policy"),
                false));

        // Contact
        OFFLINE_ANSWERS.put("contact", new ChatResponse(
                "You can reach our support team at support@agrimart.com or call us at 1800-123-4567 (9 AM - 6 PM).",
                List.of("Store locations", "Business hours"),
                false));
        OFFLINE_ANSWERS.put("support", OFFLINE_ANSWERS.get("contact"));
    }

    // Simple IP-based Rate Limiting (Reset every minute)
    private final Map<String, RequestCounter> rateLimitMap = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_MINUTE = 15; // Increased slightly for guided flow

    private static class RequestCounter {
        int count;
        long startTime;

        RequestCounter(int count, long startTime) {
            this.count = count;
            this.startTime = startTime;
        }
    }

    public ChatResponse processChat(String ipAddress, ChatRequest chatRequest) {
        // 1. Rate Limiting Check
        if (isRateLimited(ipAddress)) {
            return new ChatResponse("You are sending messages too quickly. Please try again in a minute.", List.of(),
                    false);
        }

        // 2. Input Validation
        if (chatRequest.getMessage() == null || chatRequest.getMessage().trim().isEmpty()) {
            return new ChatResponse("Please provide a message.");
        }

        String rawMessage = chatRequest.getMessage().trim().toLowerCase();

        // 3. OFFLINE INTENT MATCHING (Zero Token Cost)
        // Check for exact keywords first
        for (Map.Entry<String, ChatResponse> entry : OFFLINE_ANSWERS.entrySet()) {
            if (rawMessage.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // 4. Fallback to Groq AI
        String cleanMessage = chatRequest.getMessage().trim();
        if (cleanMessage.length() > 300) {
            return new ChatResponse("Message is too long (max 300 characters).");
        }
        cleanMessage = cleanMessage.replaceAll("<[^>]*>", "");

        return callAiApi(cleanMessage);
    }

    private boolean isRateLimited(String ipAddress) {
        long now = Instant.now().getEpochSecond();
        rateLimitMap.compute(ipAddress, (key, counter) -> {
            if (counter == null || (now - counter.startTime) > 60) {
                return new RequestCounter(1, now);
            }
            counter.count++;
            return counter;
        });

        return rateLimitMap.get(ipAddress).count > MAX_REQUESTS_PER_MINUTE;
    }

    private ChatResponse callAiApi(String userMessage) {
        if (apiKey == null || apiKey.isEmpty() || "NOT_FOUND".equals(apiKey)) {
            // Offline fallback if KEY missing
            return new ChatResponse(
                    "I am currently mostly offline. Try asking about 'delivery', 'payment', or 'returns'.",
                    List.of("Delivery options", "Payment methods", "Return policy"), false);
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", apiModel);
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", SYSTEM_PROMPT),
                    Map.of("role", "user", "content", userMessage)));
            requestBody.put("temperature", 0.4);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_URL, entity, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("choices")) {
                List choices = (List) response.getBody().get("choices");
                if (!choices.isEmpty()) {
                    Map firstChoice = (Map) choices.get(0);
                    Map message = (Map) firstChoice.get("message");
                    String content = (String) message.get("content");
                    // AI Response -> No static suggestions, or maybe generic ones?
                    // For now, no suggestions on AI replies to keep it clean, or could add "Back to
                    // menu"
                    return new ChatResponse(content, List.of(), true);
                }
            }

            return new ChatResponse("I couldn't process that. Please try again.", List.of(), false);

        } catch (Exception e) {
            System.err.println("AI API Error (Groq): " + e.getMessage());
            return new ChatResponse(
                    "Our AI assistant is temporarily unavailable. Try asking about 'delivery' or 'returns'.",
                    List.of("Delivery info", "Return policy"), false);
        }
    }
}
