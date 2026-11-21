from __future__ import annotations

import base64
import binascii
import json
from dataclasses import dataclass
from typing import Any


# Exotel AgentStream supports 8 kHz mono linear16 PCM by default.
# Media payloads are expected to be 100 ms chunks (3,200 bytes) or larger,
# up to 100,000 bytes, in multiples of 320 bytes.
MIN_AUDIO_BYTES = 3_200
MAX_AUDIO_BYTES = 100_000
AUDIO_FRAME_MULTIPLE = 320


class ExotelProtocolError(ValueError):
    """Raised when an Exotel Voicebot frame is malformed or unsafe."""


@dataclass(frozen=True)
class ExotelEvent:
    kind: str
    stream_id: str | None = None
    audio_pcm: bytes | None = None
    digit: str | None = None


def _stream_id(payload: dict[str, Any]) -> str | None:
    value = payload.get("stream_sid")
    if isinstance(value, str) and value:
        return value
    return None


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

    stream_id = _stream_id(payload)

    if kind == "start":
        start = payload.get("start")
        if not isinstance(start, dict):
            raise ExotelProtocolError("Start frame is missing start metadata")
        stream_id = _stream_id(start) or stream_id
        if not stream_id:
            raise ExotelProtocolError("Start frame is missing stream_sid")
        return ExotelEvent(kind=kind, stream_id=stream_id)

    if kind in {"connected", "stop", "clear"}:
        return ExotelEvent(kind=kind, stream_id=stream_id)

    if kind == "media":
        media = payload.get("media")
        if not isinstance(media, dict) or not isinstance(media.get("payload"), str):
            raise ExotelProtocolError("Media frame has no base64 payload")
        try:
            audio = base64.b64decode(media["payload"], validate=True)
        except (binascii.Error, ValueError) as exc:
            raise ExotelProtocolError("Invalid media base64") from exc
        if (
            len(audio) < MIN_AUDIO_BYTES
            or len(audio) > MAX_AUDIO_BYTES
            or len(audio) % AUDIO_FRAME_MULTIPLE != 0
        ):
            raise ExotelProtocolError("Audio frame size is outside Exotel limits")
        if not stream_id:
            raise ExotelProtocolError("Media frame is missing stream_sid")
        return ExotelEvent(kind=kind, stream_id=stream_id, audio_pcm=audio)

    if kind == "dtmf":
        dtmf = payload.get("dtmf", {})
        digit = dtmf.get("digit") if isinstance(dtmf, dict) else None
        if digit is not None and (not isinstance(digit, str) or len(digit) > 1):
            raise ExotelProtocolError("Invalid DTMF digit")
        return ExotelEvent(kind=kind, stream_id=stream_id, digit=digit)

    if kind == "mark":
        return ExotelEvent(kind=kind, stream_id=stream_id)

    raise ExotelProtocolError("Unsupported event type")


def make_media_frame(stream_id: str, audio_pcm: bytes) -> str:
    """Encode a validated PCM chunk as an Exotel Voicebot outbound media frame."""
    if not stream_id:
        raise ExotelProtocolError("stream_id is required")
    if (
        len(audio_pcm) < MIN_AUDIO_BYTES
        or len(audio_pcm) > MAX_AUDIO_BYTES
        or len(audio_pcm) % AUDIO_FRAME_MULTIPLE != 0
    ):
        raise ExotelProtocolError("Audio frame size is outside Exotel limits")
    return json.dumps(
        {
            "event": "media",
            "stream_sid": stream_id,
            "media": {"payload": base64.b64encode(audio_pcm).decode("ascii")},
        },
        separators=(",", ":"),
    )
