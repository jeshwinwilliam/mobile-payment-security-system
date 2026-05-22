# Mobile Payment Security System

Mobile Payment Security System is a backend-focused Java project designed to simulate secure mobile payment workflows, device-based authentication, fraud detection, and real-time payment authorization.

The project focuses on building a production-style payment security pipeline with layered backend services for authentication, risk analysis, and transaction validation.

## What This Project Demonstrates

* Device onboarding with IMEI-based device registration
* Multi-factor authentication using PIN and biometric validation
* Real-time payment risk scoring based on transaction amount, device trust, merchant type, and location changes
* OTP-based step-up authentication for suspicious transactions
* Fraud alert generation for blocked or high-risk payment attempts
* REST-style backend API workflows
* Production-oriented backend architecture and service design

## Tech Stack

* Java 25
* Built-in Java HttpServer for lightweight REST APIs
* In-memory persistence for local execution and demo workflows

## Project Structure

```text
mobile-payment-security-system/
├── README.md
├── resume-project-entry.md
├── sample-requests.http
└── src/com/jeshwin/mobilepayments/
```

## Running the Project

```bash
cd /Users/jeshwinwilliam/Documents/Playground/mobile-payment-security-system

mkdir -p out

find src -name '*.java' -print0 | xargs -0 javac --release 25 -d out

java -cp out com.jeshwin.mobilepayments.MobilePaymentSecurityApplication
```

Server starts at:

```text
http://localhost:8080
```

## API Endpoints

### Health Check

```bash
curl http://localhost:8080/api/health
```

### Register a Device

```bash
curl -X POST http://localhost:8080/api/devices/register \
-H "Content-Type: application/json" \
-d '{
  "userId":"jeshwin",
  "deviceId":"IMEI-490154203237518",
  "deviceFingerprint":"PIXEL8-ANDROID15",
  "pin":"4321",
  "biometricToken":"BIO-OK",
  "location":"Oklahoma City"
}'
```

### Login from Trusted Device

```bash
curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{
  "userId":"jeshwin",
  "deviceId":"IMEI-490154203237518",
  "pin":"4321",
  "biometricToken":"BIO-OK"
}'
```

### Initiate Low-Risk Payment

```bash
curl -X POST http://localhost:8080/api/payments/initiate \
-H "Content-Type: application/json" \
-d '{
  "userId":"jeshwin",
  "deviceId":"IMEI-490154203237518",
  "amount":"120.50",
  "currency":"USD",
  "merchant":"Spotify",
  "channel":"Wallet",
  "location":"Oklahoma City"
}'
```

### Initiate High-Risk Payment

```bash
curl -X POST http://localhost:8080/api/payments/initiate \
-H "Content-Type: application/json" \
-d '{
  "userId":"jeshwin",
  "deviceId":"IMEI-490154203237518",
  "amount":"2800.00",
  "currency":"USD",
  "merchant":"International Electronics",
  "channel":"Card",
  "location":"Miami"
}'
```

If a payment is flagged as suspicious, OTP verification is required before approval.

### Approve Payment with OTP

```bash
curl -X POST http://localhost:8080/api/payments/approve \
-H "Content-Type: application/json" \
-d '{
  "paymentId":"PASTE_PAYMENT_ID_HERE",
  "otp":"123456"
}'
```

### View Fraud Alerts

```bash
curl http://localhost:8080/api/frauds/alerts
```

## System Design Overview

The system simulates a mobile payment security gateway that validates device trust before authorizing transactions.

The backend architecture is organized into layered services for:

* Device registration
* Authentication
* Risk analysis
* Payment authorization
* Fraud monitoring

High-risk transactions trigger OTP-based step-up authentication instead of immediate approval. Fraud alerts are generated and exposed through backend APIs to simulate operational monitoring workflows used in real-world payment systems.

