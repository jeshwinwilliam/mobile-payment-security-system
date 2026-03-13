package com.jeshwin.mobilepayments.model;

import java.util.Map;

public record LoginRequest(String userId, String deviceId, String pin, String biometricToken) {
    public static LoginRequest fromMap(Map<String, String> payload) {
        return new LoginRequest(
                required(payload, "userId"),
                required(payload, "deviceId"),
                required(payload, "pin"),
                required(payload, "biometricToken"));
    }

    private static String required(Map<String, String> payload, String field) {
        String value = payload.get(field);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing field: " + field);
        }
        return value.trim();
    }
}
