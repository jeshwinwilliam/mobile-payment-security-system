package com.jeshwin.mobilepayments;

import com.jeshwin.mobilepayments.http.ApiHandler;
import com.jeshwin.mobilepayments.service.DeviceRegistryService;
import com.jeshwin.mobilepayments.service.FraudDetectionService;
import com.jeshwin.mobilepayments.service.PaymentSecurityService;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public final class MobilePaymentSecurityApplication {
    private MobilePaymentSecurityApplication() {
    }

    public static void main(String[] args) throws IOException {
        DeviceRegistryService deviceRegistryService = new DeviceRegistryService();
        FraudDetectionService fraudDetectionService = new FraudDetectionService();
        PaymentSecurityService paymentSecurityService =
                new PaymentSecurityService(deviceRegistryService, fraudDetectionService);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/health", ApiHandler.healthHandler());
        server.createContext("/api/devices/register", ApiHandler.deviceRegistrationHandler(deviceRegistryService));
        server.createContext("/api/auth/login", ApiHandler.loginHandler(deviceRegistryService));
        server.createContext("/api/payments/initiate", ApiHandler.paymentInitiationHandler(paymentSecurityService));
        server.createContext("/api/payments/approve", ApiHandler.paymentApprovalHandler(paymentSecurityService));
        server.createContext("/api/frauds/alerts", ApiHandler.fraudAlertsHandler(fraudDetectionService));
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();

        System.out.println("Mobile Payment Security System running on http://localhost:8080");
        System.out.println("Use Ctrl+C to stop the server.");
    }
}
