import base64
import json

import pytest

from app.exotel import (
    ExotelProtocolError,
    make_media_frame,
    parse_event,
)


def test_parses_start_event_with_documented_snake_case_stream_sid():
    event = parse_event(json.dumps({
        "event": "start",
        "stream_sid": "stream-123",
        "start": {"stream_sid": "stream-123", "call_sid": "call-123"},
    }))
    assert event.kind == "start"
    assert event.stream_id == "stream-123"


def test_decodes_valid_100ms_pcm_media_payload():
    pcm = b"\\x00\\x01" * 1600
    event = parse_event(json.dumps({
        "event": "media",
        "stream_sid": "stream-123",
        "media": {"payload": base64.b64encode(pcm).decode("ascii")},
    }))
    assert event.audio_pcm == pcm


def test_builds_outbound_media_frame():
    pcm = b"\\x00\\x01" * 1600
    frame = json.loads(make_media_frame("stream-123", pcm))
    assert frame["event"] == "media"
    assert frame["stream_sid"] == "stream-123"
    assert base64.b64decode(frame["media"]["payload"]) == pcm


@pytest.mark.parametrize("payload", ["%%%", ""])
def test_rejects_invalid_base64(payload):
    with pytest.raises(ExotelProtocolError):
        parse_event(json.dumps({
            "event": "media",
            "stream_sid": "stream-123",
            "media": {"payload": payload},
        }))


@pytest.mark.parametrize("size", [0, 1600, 100_160, 3_201])
def test_rejects_invalid_audio_frame_sizes(size):
    pcm = b"x" * size
    with pytest.raises(ExotelProtocolError):
        parse_event(json.dumps({
            "event": "media",
            "stream_sid": "stream-123",
            "media": {"payload": base64.b64encode(pcm).decode("ascii")},
        }))


def test_rejects_media_without_stream_id():
    pcm = b"x" * 3200
    with pytest.raises(ExotelProtocolError):
        parse_event(json.dumps({
            "event": "media",
            "media": {"payload": base64.b64encode(pcm).decode("ascii")},
        }))


def test_rejects_unknown_event():
    with pytest.raises(ExotelProtocolError):
        parse_event(json.dumps({"event": "mystery"}))


def test_rejects_start_without_stream_id():
    with pytest.raises(ExotelProtocolError):
        parse_event(json.dumps({"event": "start", "start": {}}))
