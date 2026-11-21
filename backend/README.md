# Callvanta Voice Backend

Python/FastAPI foundation for the **existing-number conditional-forwarding** AI receptionist route.

## Current status

This is an integration scaffold, **not a working AI receptionist yet**. It provides health/readiness endpoints, a token-protected Exotel WebSocket endpoint, event parsing, PCM payload validation, and unit tests. It does not yet transcribe speech, call an LLM, synthesize speech, play an AI greeting, or safely hand calls to a human. Do not point live Airtel forwarding at this endpoint.

## Architecture

Airtel conditional forwarding sends unanswered/busy/unreachable calls to a telephony-provider destination. The provider's Voicebot Applet connects to:

`wss://YOUR_HOST/v1/telephony/exotel/media?token=YOUR_LONG_RANDOM_TOKEN`

The gateway expects Exotel Voicebot events: Connected, Start, Media, DTMF, Clear, and Stop. Audio is base64-encoded 16-bit linear PCM, 8 kHz, mono. The decoded media is currently validated and discarded; it is not stored or logged.

Exotel is only a media relay. Speech recognition, conversation orchestration, LLM reasoning, and text-to-speech must be supplied by Callvanta. The Voicebot Applet may not be enabled on every Exotel account; confirm enablement with Exotel before relying on it.

## Local development

Requires Python 3.11+.

```bash
cd backend
python -m venv .venv
# Activate the virtual environment, then:
pip install -e ".[dev]"
cp .env.example .env
# Set CALLVANTA_EXOTEL_WS_TOKEN to a long random value.
uvicorn app.main:app --host 127.0.0.1 --port 8000
pytest
```

- `GET /health`: process health.
- `GET /ready`: reports whether WebSocket authentication is configured and explicitly reports that the voice pipeline is not implemented.
- `GET /v1/telephony/exotel/status`: provider integration status.
- `WS /v1/telephony/exotel/media?token=...`: token-protected Exotel media endpoint.

For local unit tests, no provider account or credentials are needed.

## Security

- Keep the WebSocket token and all provider/AI credentials in deployment secrets.
- Use HTTPS/WSS with a valid public TLS certificate in hosted environments.
- Do not put secrets in Android resources, source control, APKs, or logs.
- Do not log raw caller audio, transcripts, phone numbers, or provider payloads.
- Rotate the token if it is exposed. A query-string token must be redacted from reverse-proxy and application access logs.
- Do not enable carrier forwarding to this scaffold. The readiness endpoint intentionally identifies the voice pipeline as unimplemented.

## Next implementation gates

1. Confirm Exotel account eligibility, Voicebot Applet enablement, supported number type, pricing, and webhook security details.
2. Add a streaming STT adapter for 8 kHz mono PCM.
3. Add a conversation orchestrator with strict timeouts, interruption/barge-in handling, and caller-requested exit.
4. Add streaming TTS and correctly formatted outbound media frames.
5. Play an AI identity/privacy disclosure before collecting caller information.
6. Add a tested fallback/escalation flow and provider Passthru integration.
7. Test on a provider test number, then test Airtel conditional forwarding to that test destination. Never activate it on the owner's personal number without explicit approval.
