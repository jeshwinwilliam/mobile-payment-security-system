package com.jeshwin.mobilepayments.model;

import java.math.BigDecimal;
import java.util.Map;

public record PaymentInitiationRequest(
        String userId,
        String deviceId,
        BigDecimal amount,
        String currency,
        String merchant,
        String channel,
        String location) {

    public static PaymentInitiationRequest fromMap(Map<String, String> payload) {
        return new PaymentInitiationRequest(
                required(payload, "userId"),
                required(payload, "deviceId"),
                new BigDecimal(required(payload, "amount")),
                required(payload, "currency"),
                required(payload, "merchant"),
                required(payload, "channel"),
                required(payload, "location"));
    }

    private static String required(Map<String, String> payload, String field) {
        String value = payload.get(field);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing field: " + field);
        }
        return value.trim();
    }
}
