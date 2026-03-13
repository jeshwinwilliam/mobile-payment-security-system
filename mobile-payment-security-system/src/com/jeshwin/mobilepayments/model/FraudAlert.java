package com.jeshwin.mobilepayments.model;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public record FraudAlert(
        String paymentId,
        String userId,
        String deviceId,
        String reason,
        int riskScore,
        Instant createdAt) {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("paymentId", paymentId);
        map.put("userId", userId);
        map.put("deviceId", deviceId);
        map.put("reason", reason);
        map.put("riskScore", riskScore);
        map.put("createdAt", createdAt.toString());
        return map;
    }
}
