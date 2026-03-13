package com.jeshwin.mobilepayments.model;

import java.util.LinkedHashMap;
import java.util.Map;

public record PaymentResponse(
        String status,
        String message,
        PaymentRecord paymentRecord,
        String otpHint) {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", status);
        map.put("message", message);
        map.put("payment", paymentRecord.toMap());
        if (otpHint != null) {
            map.put("otpHint", otpHint);
        }
        return map;
    }
}
