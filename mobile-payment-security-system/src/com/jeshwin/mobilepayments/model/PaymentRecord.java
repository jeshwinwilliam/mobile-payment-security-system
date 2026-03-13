package com.jeshwin.mobilepayments.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public record PaymentRecord(
        String paymentId,
        String userId,
        String deviceId,
        BigDecimal amount,
        String currency,
        String merchant,
        String channel,
        String location,
        int riskScore,
        PaymentDecision decision,
        String reason,
        Instant createdAt) {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("paymentId", paymentId);
        map.put("userId", userId);
        map.put("deviceId", deviceId);
        map.put("amount", amount.toPlainString());
        map.put("currency", currency);
        map.put("merchant", merchant);
        map.put("channel", channel);
        map.put("location", location);
        map.put("riskScore", riskScore);
        map.put("decision", decision.name());
        map.put("reason", reason);
        map.put("createdAt", createdAt.toString());
        return map;
    }
}
