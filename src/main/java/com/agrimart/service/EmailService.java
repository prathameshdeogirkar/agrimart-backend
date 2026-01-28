package com.agrimart.service;

import com.agrimart.entity.Order;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final InvoiceService invoiceService;

    @Async
    public void sendOrderConfirmation(Order order) {
        try {
            System.out.println("Sending email to: " + order.getUser().getEmail());

            // Generate PDF
            byte[] pdfBytes = invoiceService.generateInvoice(order);

            // Create Email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Use Public ID if available
            String displayId = (order.getPublicOrderId() != null) ? order.getPublicOrderId()
                    : String.valueOf(order.getId());

            helper.setTo(order.getUser().getEmail());
            helper.setSubject("Order Confirmation - #" + displayId);
            helper.setText("Dear " + order.getFullName() + ",\n\n" +
                    "Thank you for your order! Please find your invoice attached.\n\n" +
                    "Order ID: " + displayId + "\n" +
                    "Total Amount: ₹" + order.getTotalAmount() + "\n\n" +
                    "Regards,\n" +
                    "Agrimart Team");

            // Attach PDF
            helper.addAttachment("Invoice_" + displayId + ".pdf", new ByteArrayResource(pdfBytes));

            // Send
            mailSender.send(message);
            System.out.println("Email sent successfully to: " + order.getUser().getEmail());

        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
