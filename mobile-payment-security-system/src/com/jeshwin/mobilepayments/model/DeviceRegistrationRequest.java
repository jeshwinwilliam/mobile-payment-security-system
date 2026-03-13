package com.jeshwin.mobilepayments.model;

import java.util.Map;

public record DeviceRegistrationRequest(
        String userId,
        String deviceId,
        String deviceFingerprint,
        String pin,
        String biometricToken,
        String location) {

    public static DeviceRegistrationRequest fromMap(Map<String, String> payload) {
        return new DeviceRegistrationRequest(
                required(payload, "userId"),
                required(payload, "deviceId"),
                required(payload, "deviceFingerprint"),
                required(payload, "pin"),
                required(payload, "biometricToken"),
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
