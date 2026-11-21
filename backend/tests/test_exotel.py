import base64
import json

import pytest

from app.exotel import ExotelProtocolError, parse_event


def test_parses_start_event_and_stream_sid():
    event = parse_event(json.dumps({
        "event": "start",
        "start": {"streamSid": "stream-123"},
    }))
    assert event.kind == "start"
    assert event.stream_id == "stream-123"


def test_decodes_media_payload_to_pcm():
    pcm = b"\\x00\\x01" * 1600
    event = parse_event(json.dumps({
        "event": "media",
        "streamSid": "stream-123",
        "media": {"payload": base64.b64encode(pcm).decode("ascii")},
    }))
    assert event.audio_pcm == pcm


def test_rejects_invalid_base64():
    with pytest.raises(ExotelProtocolError):
        parse_event(json.dumps({"event": "media", "media": {"payload": "%%%"}}))


def test_rejects_oversized_audio():
    pcm = b"x" * 100_001
    with pytest.raises(ExotelProtocolError):
        parse_event(json.dumps({
            "event": "media",
            "media": {"payload": base64.b64encode(pcm).decode("ascii")},
        }))


def test_rejects_unknown_event():
    with pytest.raises(ExotelProtocolError):
        parse_event(json.dumps({"event": "mystery"}))
