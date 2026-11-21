from __future__ import annotations

import base64
import binascii
import json
from dataclasses import dataclass
from typing import Any


MAX_AUDIO_BYTES = 100_000
MIN_AUDIO_BYTES = 3_200


class ExotelProtocolError(ValueError):
    """Raised when an Exotel Voicebot frame is malformed or unsafe."""


@dataclass(frozen=True)
class ExotelEvent:
    kind: str
    stream_id: str | None = None
    audio_pcm: bytes | None = None
    digit: str | None = None


def parse_event(raw: str) -> ExotelEvent:
    try:
        payload: Any = json.loads(raw)
    except json.JSONDecodeError as exc:
        raise ExotelProtocolError("Invalid JSON frame") from exc

    if not isinstance(payload, dict):
        raise ExotelProtocolError("Frame must be a JSON object")

    kind = payload.get("event")
    if not isinstance(kind, str):
        raise ExotelProtocolError("Missing event type")

    kind = kind.lower()
    stream_id = payload.get("stream_sid") or payload.get("streamSid")

    if kind in {"connected", "start", "stop", "clear"}:
        if kind == "start":
            start = payload.get("start", {})
            if isinstance(start, dict):
                stream_id = start.get("streamSid") or stream_id
        return ExotelEvent(kind=kind, stream_id=stream_id)

    if kind == "media":
        media = payload.get("media")
        if not isinstance(media, dict) or not isinstance(media.get("payload"), str):
            raise ExotelProtocolError("Media frame has no base64 payload")
        try:
            audio = base64.b64decode(media["payload"], validate=True)
        except (binascii.Error, ValueError) as exc:
            raise ExotelProtocolError("Invalid media base64") from exc
        if not audio or len(audio) > MAX_AUDIO_BYTES:
            raise ExotelProtocolError("Audio frame size is outside limits")
        return ExotelEvent(kind=kind, stream_id=stream_id, audio_pcm=audio)

    if kind == "dtmf":
        dtmf = payload.get("dtmf", {})
        digit = dtmf.get("digit") if isinstance(dtmf, dict) else None
        return ExotelEvent(kind=kind, stream_id=stream_id, digit=digit)

    raise ExotelProtocolError("Unsupported event type")
