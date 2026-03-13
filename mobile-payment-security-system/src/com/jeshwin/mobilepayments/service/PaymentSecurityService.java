package com.jeshwin.mobilepayments.service;

import com.jeshwin.mobilepayments.model.DeviceProfile;
import com.jeshwin.mobilepayments.model.PaymentApprovalRequest;
import com.jeshwin.mobilepayments.model.PaymentDecision;
import com.jeshwin.mobilepayments.model.PaymentInitiationRequest;
import com.jeshwin.mobilepayments.model.PaymentRecord;
import com.jeshwin.mobilepayments.model.PaymentResponse;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PaymentSecurityService {
    private static final String DEMO_OTP = "123456";

    private final DeviceRegistryService deviceRegistryService;
    private final FraudDetectionService fraudDetectionService;
    private final Map<String, PaymentRecord> payments = new ConcurrentHashMap<>();

    public PaymentSecurityService(
            DeviceRegistryService deviceRegistryService,
            FraudDetectionService fraudDetectionService) {
        this.deviceRegistryService = deviceRegistryService;
        this.fraudDetectionService = fraudDetectionService;
    }

    public PaymentResponse initiatePayment(PaymentInitiationRequest request) {
        DeviceProfile deviceProfile =
                deviceRegistryService.findRequiredDevice(request.userId(), request.deviceId());
        FraudDetectionService.RiskAssessment assessment =
                fraudDetectionService.assess(request, deviceProfile);

        String paymentId = UUID.randomUUID().toString();
        PaymentRecord paymentRecord = new PaymentRecord(
                paymentId,
                request.userId(),
                request.deviceId(),
                request.amount(),
                request.currency(),
                request.merchant(),
                request.channel(),
                request.location(),
                assessment.riskScore(),
                assessment.decision(),
                assessment.reason(),
                Instant.now());
        payments.put(paymentId, paymentRecord);

        if (assessment.decision() == PaymentDecision.BLOCKED) {
            fraudDetectionService.recordAlert(
                    paymentId, request.userId(), request.deviceId(), assessment.reason(), assessment.riskScore());
            return new PaymentResponse("BLOCKED", "Payment blocked by fraud controls", paymentRecord, null);
        }

        if (assessment.decision() == PaymentDecision.CHALLENGE_OTP) {
            return new PaymentResponse(
                    "CHALLENGE_OTP",
                    "Payment requires OTP verification before approval",
                    paymentRecord,
                    "Use demo OTP 123456");
        }

        return new PaymentResponse("APPROVED", "Payment approved", paymentRecord, null);
    }

    public PaymentResponse approvePayment(PaymentApprovalRequest request) {
        PaymentRecord existingRecord = payments.get(request.paymentId());
        if (existingRecord == null) {
            throw new IllegalArgumentException("Payment ID not found");
        }

        if (existingRecord.decision() != PaymentDecision.CHALLENGE_OTP) {
            throw new IllegalArgumentException("This payment does not require OTP approval");
        }

        if (!DEMO_OTP.equals(request.otp())) {
            fraudDetectionService.recordAlert(
                    existingRecord.paymentId(),
                    existingRecord.userId(),
                    existingRecord.deviceId(),
                    "Invalid OTP submitted for challenged transaction",
                    existingRecord.riskScore());
            throw new IllegalArgumentException("Invalid OTP");
        }

        PaymentRecord approvedRecord = new PaymentRecord(
                existingRecord.paymentId(),
                existingRecord.userId(),
                existingRecord.deviceId(),
                existingRecord.amount(),
                existingRecord.currency(),
                existingRecord.merchant(),
                existingRecord.channel(),
                existingRecord.location(),
                existingRecord.riskScore(),
                PaymentDecision.APPROVED,
                "OTP validated and payment approved",
                existingRecord.createdAt());
        payments.put(approvedRecord.paymentId(), approvedRecord);
        return new PaymentResponse("APPROVED", "OTP validated and payment approved", approvedRecord, null);
    }
}
