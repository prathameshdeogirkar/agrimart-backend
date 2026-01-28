package com.agrimart.service;

import com.agrimart.dto.PaymentVerificationRequest;
import com.agrimart.entity.Cart;
import com.agrimart.entity.Order;
import com.agrimart.entity.User;
import com.agrimart.repository.CartRepository;
import com.agrimart.repository.OrderRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private RazorpayClient razorpayClient;

    private final CartRepository cartRepository;
    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;
    private final EmailService emailService;

    @PostConstruct
    public void init() throws RazorpayException {
        this.razorpayClient = new RazorpayClient(keyId, keySecret);
    }

    public Map<String, Object> createOrder(User user) throws RazorpayException {
        System.out.println("Processing Payment for User: " + user.getEmail());

        // 1. Calculate Total Amount from Cart
        List<Cart> cartItems = cartRepository.findByUser(user);
        System.out.println("Backend Cart Size: " + cartItems.size());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double totalAmount = cartItems.stream()
                .mapToDouble(c -> c.getProduct().getPrice() * c.getQuantity())
                .sum();

        // 2. Create Razorpay Order
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (totalAmount * 100)); // Amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);

        return Map.of(
                "orderId", razorpayOrder.get("id"),
                "keyId", keyId,
                "amount", razorpayOrder.get("amount"),
                "currency", razorpayOrder.get("currency"));
    }

    @Transactional
    public Order verifyPayment(User user, PaymentVerificationRequest request) {
        try {
            // 1. Verify Signature
            String signature = request.getRazorpaySignature();
            String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();

            verifySignature(payload, signature, keySecret);

            // 2. Signature Valid -> Place Order
            Order order = checkoutService.checkout(user, request.getCheckoutRequest());

            // 3. Update Order with Payment Details
            order.setRazorpayOrderId(request.getRazorpayOrderId());
            order.setRazorpayPaymentId(request.getRazorpayPaymentId());
            order.setStatus("PAID"); // Mark as PAID

            Order savedOrder = orderRepository.save(order);

            // 📧 Send Invoice Email (Async)
            emailService.sendOrderConfirmation(savedOrder);

            return savedOrder;

        } catch (Exception e) {
            throw new RuntimeException("Payment verification failed: " + e.getMessage());
        }
    }

    private void verifySignature(String payload, String signature, String secret) throws SignatureException {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(payload.getBytes());

            StringBuilder result = new StringBuilder();
            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }

            if (!result.toString().equals(signature)) {
                throw new SignatureException("Invalid Razorpay signature");
            }
        } catch (Exception e) {
            throw new SignatureException("Error verifying signature: " + e.getMessage());
        }
    }
}
