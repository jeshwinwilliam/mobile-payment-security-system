# Mobile Payment Security System

Mobile Payment Security System is a Java project built for Jeshwin William James to showcase secure mobile payment workflows, device-based authentication, fraud detection, and real-time payment authorization decisions.

## What this project demonstrates

- Device onboarding with IMEI-style device registration and fingerprint binding
- Multi-factor login using PIN and biometric token validation
- Real-time payment risk scoring based on amount, merchant, device trust, and location change
- Step-up authentication using OTP for suspicious transactions
- Fraud alert generation for blocked or risky payment attempts
- Clean API design and a project structure that is easy to discuss in interviews

## Tech stack

- Java 25
- Built-in `HttpServer` for lightweight REST endpoints
- In-memory storage for demo-friendly local execution

## Project structure

```text
mobile-payment-security-system/
  README.md
  resume-project-entry.md
  sample-requests.http
  src/com/jeshwin/mobilepayments/
```

## Run locally

```bash
cd /Users/jeshwinwilliam/Documents/Playground/mobile-payment-security-system
mkdir -p out
find src -name '*.java' -print0 | xargs -0 javac --release 25 -d out
java -cp out com.jeshwin.mobilepayments.MobilePaymentSecurityApplication
```

Server starts at `http://localhost:8080`.

## API endpoints

### Health check

```bash
curl http://localhost:8080/api/health
```

### Register a device

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

### Login from trusted device

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

### Initiate a low-risk payment

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

### Initiate a higher-risk payment

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

If a payment is challenged, approve it with OTP `123456`.

```bash
curl -X POST http://localhost:8080/api/payments/approve \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId":"PASTE_PAYMENT_ID_HERE",
    "otp":"123456"
  }'
```

### View fraud alerts

```bash
curl http://localhost:8080/api/frauds/alerts
```

## How to explain this in interviews

- The project simulates a payment security gateway that validates device trust before allowing mobile transactions.
- The backend uses layered services for registration, authentication, risk scoring, and payment authorization.
- Suspicious transactions trigger OTP step-up verification instead of immediate approval.
- Fraud alerts are stored and exposed through an API to mimic operational monitoring workflows.

## Resume-ready project title

**Mobile Payment Security System | Java, REST APIs, Authentication, Fraud Detection**

See [resume-project-entry.md](/Users/jeshwinwilliam/Documents/Playground/mobile-payment-security-system/resume-project-entry.md) for polished resume bullet points.
