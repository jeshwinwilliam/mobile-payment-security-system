package com.jeshwin.mobilepayments.model;

import java.util.LinkedHashMap;
import java.util.Map;

public record LoginResponse(String status, String message, DeviceProfile deviceProfile) {
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", status);
        map.put("message", message);
        map.put("device", deviceProfile.toMap());
        return map;
    }
}
