from __future__ import annotations

import hmac
import os
from typing import Any

from fastapi import FastAPI, WebSocket, WebSocketDisconnect, status

from .exotel import ExotelEvent, ExotelProtocolError, parse_event

app = FastAPI(
    title="Callvanta Voice Gateway",
    version="0.1.0",
    description="Provider-facing media gateway for conditional-forwarded calls.",
)


def _configured_token() -> str:
    return os.getenv("CALLVANTA_EXOTEL_WS_TOKEN", "").strip()


@app.get("/health")
async def health() -> dict[str, str]:
    return {"status": "ok", "service": "callvanta-voice-gateway"}


@app.get("/ready")
async def ready() -> dict[str, Any]:
    token_configured = bool(_configured_token())
    return {
        "status": "ready" if token_configured else "not_configured",
        "provider": "exotel",
        "websocket_auth_configured": token_configured,
        "voice_pipeline": "not_implemented",
    }


@app.websocket("/v1/telephony/exotel/media")
async def exotel_media(websocket: WebSocket) -> None:
    expected = _configured_token()
    supplied = websocket.query_params.get("token", "")
    if not expected or not hmac.compare_digest(supplied, expected):
        await websocket.close(code=status.WS_1008_POLICY_VIOLATION)
        return

    await websocket.accept()
    session_id: str | None = None
    try:
        while True:
            raw = await websocket.receive_text()
            try:
                event = parse_event(raw)
            except ExotelProtocolError:
                # Do not echo provider payloads or caller data into logs.
                await websocket.close(code=status.WS_1003_UNSUPPORTED_DATA)
                return

            if event.kind == "start":
                session_id = event.stream_id
            elif event.kind == "clear":
                # Conversation state reset hook. No transcript/audio is retained.
                continue
            elif event.kind == "media":
                # TODO: forward decoded PCM to the configured streaming STT pipeline.
                # The current milestone deliberately does not pretend to answer calls.
                continue
            elif event.kind in {"stop", "disconnect"}:
                return
            elif event.kind == "dtmf":
                # DTMF handling will be wired into the conversation orchestrator.
                continue
    except WebSocketDisconnect:
        return


@app.get("/v1/telephony/exotel/status")
async def exotel_status() -> dict[str, str]:
    return {
        "provider": "exotel",
        "integration": "websocket_protocol_scaffold",
        "voice_pipeline": "not_implemented",
    }
