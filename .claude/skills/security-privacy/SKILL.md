---
name: security-privacy
description: Review Callvanta changes for secrets exposure, caller-data minimization, consent, Android permissions, backend security, and privacy risks.
---

# Security and Privacy Review

Treat phone numbers, call metadata, transcripts, recordings, and summaries as sensitive.

## Required checks
- Is each permission/role necessary, explained, and requested at the correct time?
- Is only the minimum caller data collected and retained?
- Is remote processing disclosed and appropriately authorized?
- Are recording/transcription behaviors transparent and consent-aware?
- Can users review and delete stored records?
- Are secrets absent from source, APK, logs, issues, screenshots, and test fixtures?
- Are network requests authenticated, validated, rate-limited, and protected in transit?
- Are test fixtures synthetic and non-identifying?

## Prohibited patterns
- Covert recording or transmission.
- Permission or platform restriction bypasses.
- Provider keys in Android resources, BuildConfig, source files, or committed config.
- Logging raw call content or authentication tokens.
- Presenting inferred AI content as verified caller identity.

Report severity, exploitability, affected component, evidence, and remediation. Block releases for unresolved critical/high-risk issues.
