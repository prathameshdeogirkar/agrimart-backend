package com.agrimart.service;

import com.agrimart.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final InvoiceService invoiceService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    @Value("${brevo.api.url}")
    private String apiUrl;

    @Async
    public void sendOrderConfirmation(Order order) {
        try {
            // Trim the key to prevent 401 from trailing spaces
            String cleanApiKey = (apiKey != null) ? apiKey.trim() : "";

            System.out.println("🚀 Preparing to send Brevo Email to: " + order.getUser().getEmail());

            if (cleanApiKey.isEmpty()) {
                System.err.println("❌ ERROR: BREVO_API_KEY is missing or empty!");
                return;
            }

            // 1. Generate PDF Invoice
            byte[] pdfBytes = invoiceService.generateInvoice(order);
            String base64Content = Base64.getEncoder().encodeToString(pdfBytes);

            // 2. Prepare Display ID
            String displayId = (order.getPublicOrderId() != null) ? order.getPublicOrderId()
                    : String.valueOf(order.getId());

            // 3. Build Brevo API Payload (JSON)
            Map<String, Object> payload = new HashMap<>();

            // Sender
            Map<String, String> sender = new HashMap<>();
            sender.put("name", senderName);
            sender.put("email", senderEmail);
            payload.put("sender", sender);

            // Recipient
            Map<String, String> to = new HashMap<>();
            to.put("email", order.getUser().getEmail());
            to.put("name", order.getFullName());
            payload.put("to", Collections.singletonList(to));

            // Content
            payload.put("subject", "Order Confirmation - #" + displayId);
            payload.put("htmlContent",
                    "<html><body>" +
                            "<h3>Dear " + order.getFullName() + ",</h3>" +
                            "<p>Thank you for shopping with <strong>Agrimart</strong>! Your order has been placed successfully.</p>"
                            +
                            "<p><strong>Order ID:</strong> " + displayId + "<br>" +
                            "<strong>Total Amount:</strong> ₹" + order.getTotalAmount() + "</p>" +
                            "<p>Please find your invoice attached for your records.</p>" +
                            "<br><p>Regards,<br><strong>The Agrimart Team</strong></p>" +
                            "</body></html>");

            // Attachment
            Map<String, String> attachment = new HashMap<>();
            attachment.put("content", base64Content);
            attachment.put("name", "Invoice_" + displayId + ".pdf");
            payload.put("attachment", Collections.singletonList(attachment));

            // 4. Set Headers (Strict Brevo v3 Requirements)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("api-key", cleanApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            // 5. Execute API Call
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ Email sent successfully via Brevo API.");
            } else {
                System.err.println("❌ Brevo API Error: " + response.getStatusCode() + " - " + response.getBody());
            }

        } catch (HttpClientErrorException e) {
            System.err.println("❌ Brevo API 401/4xx Error: " + e.getStatusCode());
            System.err.println("❌ Response Body: " + e.getResponseBodyAsString());
            System.err.println(
                    "👉 Check if your BREVO_API_KEY is a v3 API Key (starts with xkeysib-) and contains no spaces.");
        } catch (Exception e) {
            System.err.println("❌ Failed to send email via Brevo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
