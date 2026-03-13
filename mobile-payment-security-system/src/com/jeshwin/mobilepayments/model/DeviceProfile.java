package com.jeshwin.mobilepayments.model;

import java.util.LinkedHashMap;
import java.util.Map;

public record DeviceProfile(
        String userId,
        String deviceId,
        String deviceFingerprint,
        String pin,
        String biometricToken,
        String location,
        int trustScore) {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("userId", userId);
        map.put("deviceId", deviceId);
        map.put("deviceFingerprint", deviceFingerprint);
        map.put("location", location);
        map.put("trustScore", trustScore);
        return map;
    }
}
