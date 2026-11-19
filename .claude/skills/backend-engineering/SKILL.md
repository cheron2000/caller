---
name: backend-engineering
description: Design and implement Callvanta's optional Python FastAPI backend, secure provider integration, API contracts, validation, and resilient AI operations.
---

# Backend Engineering Rules

The backend is optional for core call-screening and local call-record functionality.

## Responsibilities
- Authenticate app requests and authorize operations.
- Validate schemas, bound request sizes, and minimize personal data.
- Expose provider-neutral AI operations such as permitted text summarization.
- Keep provider-specific adapters behind an internal interface.
- Apply timeouts, rate limits, bounded retries, and safe error responses.
- Use privacy-aware logs that exclude caller content and secrets.

## Security
- Provider credentials exist only in deployment environment secrets.
- Never embed API keys in the APK or commit them.
- Use TLS in deployment and an explicit app authentication strategy.
- Do not accept raw call audio or transcripts unless the feature has been approved with a clear lawful/consent-aware data flow.

## Reliability
- Backend failure must not break local call UI or screening.
- Return typed, versioned responses.
- Test authentication, validation, provider timeout, rate limiting, and data minimization.
