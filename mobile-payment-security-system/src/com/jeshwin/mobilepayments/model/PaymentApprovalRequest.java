package com.jeshwin.mobilepayments.model;

import java.util.Map;

public record PaymentApprovalRequest(String paymentId, String otp) {
    public static PaymentApprovalRequest fromMap(Map<String, String> payload) {
        return new PaymentApprovalRequest(required(payload, "paymentId"), required(payload, "otp"));
    }

    private static String required(Map<String, String> payload, String field) {
        String value = payload.get(field);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing field: " + field);
        }
        return value.trim();
    }
}
