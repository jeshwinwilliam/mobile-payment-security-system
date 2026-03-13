package com.jeshwin.mobilepayments.service;

import com.jeshwin.mobilepayments.model.DeviceProfile;
import com.jeshwin.mobilepayments.model.FraudAlert;
import com.jeshwin.mobilepayments.model.PaymentDecision;
import com.jeshwin.mobilepayments.model.PaymentInitiationRequest;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class FraudDetectionService {
    private final List<FraudAlert> alerts = new CopyOnWriteArrayList<>();

    public RiskAssessment assess(PaymentInitiationRequest request, DeviceProfile deviceProfile) {
        int riskScore = 10;
        List<String> reasons = new ArrayList<>();

        if (request.amount().compareTo(new BigDecimal("1000")) > 0) {
            riskScore += 30;
            reasons.add("high transaction amount");
        }

        if (request.amount().compareTo(new BigDecimal("2500")) > 0) {
            riskScore += 25;
            reasons.add("very high transaction amount");
        }

        String merchantLower = request.merchant().toLowerCase();
        if (merchantLower.contains("international") || merchantLower.contains("electronics")) {
            riskScore += 20;
            reasons.add("high-risk merchant category");
        }

        if (!deviceProfile.location().equalsIgnoreCase(request.location())) {
            riskScore += 20;
            reasons.add("location mismatch from registered device");
        }

        if (deviceProfile.trustScore() < 70) {
            riskScore += 15;
            reasons.add("low device trust score");
        }

        PaymentDecision decision;
        String reason;
        if (riskScore >= 80) {
            decision = PaymentDecision.BLOCKED;
            reason = reasons.isEmpty() ? "payment blocked by security policy" : String.join(", ", reasons);
        } else if (riskScore >= 40) {
            decision = PaymentDecision.CHALLENGE_OTP;
            reason = reasons.isEmpty() ? "payment requires OTP verification" : String.join(", ", reasons);
        } else {
            decision = PaymentDecision.APPROVED;
            reason = reasons.isEmpty() ? "trusted transaction" : String.join(", ", reasons);
        }

        return new RiskAssessment(riskScore, decision, reason);
    }

    public void recordAlert(String paymentId, String userId, String deviceId, String reason, int riskScore) {
        alerts.add(new FraudAlert(paymentId, userId, deviceId, reason, riskScore, Instant.now()));
    }

    public Map<String, Object> toResponse() {
        List<Map<String, Object>> items = new ArrayList<>();
        for (FraudAlert alert : alerts) {
            items.add(alert.toMap());
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("count", alerts.size());
        payload.put("alerts", items);
        return payload;
    }

    public record RiskAssessment(int riskScore, PaymentDecision decision, String reason) {
    }
}
