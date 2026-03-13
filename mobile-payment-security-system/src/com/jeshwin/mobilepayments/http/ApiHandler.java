package com.jeshwin.mobilepayments.http;

import com.jeshwin.mobilepayments.model.DeviceRegistrationRequest;
import com.jeshwin.mobilepayments.model.LoginRequest;
import com.jeshwin.mobilepayments.model.PaymentApprovalRequest;
import com.jeshwin.mobilepayments.model.PaymentInitiationRequest;
import com.jeshwin.mobilepayments.service.DeviceRegistryService;
import com.jeshwin.mobilepayments.service.FraudDetectionService;
import com.jeshwin.mobilepayments.service.PaymentSecurityService;
import com.jeshwin.mobilepayments.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ApiHandler {
    private ApiHandler() {
    }

    public static HttpHandler healthHandler() {
        return exchange -> {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                writeMethodNotAllowed(exchange);
                return;
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", "UP");
            response.put("service", "mobile-payment-security-system");
            writeJson(exchange, 200, response);
        };
    }

    public static HttpHandler deviceRegistrationHandler(DeviceRegistryService deviceRegistryService) {
        return exchange -> {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                writeMethodNotAllowed(exchange);
                return;
            }

            try {
                DeviceRegistrationRequest request =
                        DeviceRegistrationRequest.fromMap(JsonUtil.parseObject(readRequestBody(exchange)));
                writeJson(exchange, 201, deviceRegistryService.registerDevice(request).toMap());
            } catch (IllegalArgumentException exception) {
                writeError(exchange, 400, exception.getMessage());
            }
        };
    }

    public static HttpHandler loginHandler(DeviceRegistryService deviceRegistryService) {
        return exchange -> {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                writeMethodNotAllowed(exchange);
                return;
            }

            try {
                LoginRequest request = LoginRequest.fromMap(JsonUtil.parseObject(readRequestBody(exchange)));
                writeJson(exchange, 200, deviceRegistryService.login(request).toMap());
            } catch (IllegalArgumentException exception) {
                writeError(exchange, 401, exception.getMessage());
            }
        };
    }

    public static HttpHandler paymentInitiationHandler(PaymentSecurityService paymentSecurityService) {
        return exchange -> {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                writeMethodNotAllowed(exchange);
                return;
            }

            try {
                PaymentInitiationRequest request =
                        PaymentInitiationRequest.fromMap(JsonUtil.parseObject(readRequestBody(exchange)));
                writeJson(exchange, 200, paymentSecurityService.initiatePayment(request).toMap());
            } catch (IllegalArgumentException exception) {
                writeError(exchange, 400, exception.getMessage());
            }
        };
    }

    public static HttpHandler paymentApprovalHandler(PaymentSecurityService paymentSecurityService) {
        return exchange -> {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                writeMethodNotAllowed(exchange);
                return;
            }

            try {
                PaymentApprovalRequest request =
                        PaymentApprovalRequest.fromMap(JsonUtil.parseObject(readRequestBody(exchange)));
                writeJson(exchange, 200, paymentSecurityService.approvePayment(request).toMap());
            } catch (IllegalArgumentException exception) {
                writeError(exchange, 400, exception.getMessage());
            }
        };
    }

    public static HttpHandler fraudAlertsHandler(FraudDetectionService fraudDetectionService) {
        return exchange -> {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                writeMethodNotAllowed(exchange);
                return;
            }

            writeJson(exchange, 200, fraudDetectionService.toResponse());
        };
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private static void writeMethodNotAllowed(HttpExchange exchange) throws IOException {
        writeError(exchange, 405, "Method not allowed");
    }

    private static void writeError(HttpExchange exchange, int statusCode, String message) throws IOException {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("error", message);
        writeJson(exchange, statusCode, payload);
    }

    private static void writeJson(HttpExchange exchange, int statusCode, Object body) throws IOException {
        byte[] response = JsonUtil.toJson(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }
}
