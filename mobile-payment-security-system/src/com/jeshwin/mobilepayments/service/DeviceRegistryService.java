package com.jeshwin.mobilepayments.service;

import com.jeshwin.mobilepayments.model.DeviceProfile;
import com.jeshwin.mobilepayments.model.DeviceRegistrationRequest;
import com.jeshwin.mobilepayments.model.LoginRequest;
import com.jeshwin.mobilepayments.model.LoginResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DeviceRegistryService {
    private final Map<String, DeviceProfile> devicesByUserAndDevice = new ConcurrentHashMap<>();

    public DeviceProfile registerDevice(DeviceRegistrationRequest request) {
        int trustScore = calculateTrustScore(request.deviceFingerprint(), request.location());
        DeviceProfile profile = new DeviceProfile(
                request.userId(),
                request.deviceId(),
                request.deviceFingerprint(),
                request.pin(),
                request.biometricToken(),
                request.location(),
                trustScore);
        devicesByUserAndDevice.put(key(request.userId(), request.deviceId()), profile);
        return profile;
    }

    public LoginResponse login(LoginRequest request) {
        DeviceProfile profile = findRequiredDevice(request.userId(), request.deviceId());

        if (!profile.pin().equals(request.pin())) {
            throw new IllegalArgumentException("Invalid PIN for registered device");
        }

        if (!profile.biometricToken().equals(request.biometricToken())) {
            throw new IllegalArgumentException("Biometric verification failed");
        }

        return new LoginResponse("SUCCESS", "Trusted device authentication completed", profile);
    }

    public DeviceProfile findRequiredDevice(String userId, String deviceId) {
        DeviceProfile profile = devicesByUserAndDevice.get(key(userId, deviceId));
        if (profile == null) {
            throw new IllegalArgumentException("Device is not registered for this user");
        }
        return profile;
    }

    private int calculateTrustScore(String deviceFingerprint, String location) {
        int score = 60;
        if (deviceFingerprint.length() >= 10) {
            score += 20;
        }
        if (!location.isBlank()) {
            score += 10;
        }
        return Math.min(score, 95);
    }

    private String key(String userId, String deviceId) {
        return userId + "::" + deviceId;
    }
}
