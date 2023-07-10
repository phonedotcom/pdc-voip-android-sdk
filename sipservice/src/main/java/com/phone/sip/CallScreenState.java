package com.phone.sip;

public enum CallScreenState {

        RINGING,
        RECEIVING,
        CALL_INITIATED,
        DIALING,
        ONGOING_CALL,
        DISCONNECTED,       // Remote disconnected would be handled here only
        INCOMING_SINGLE_CALL,
        CONNECTING,
        VIDEO_INITIATED,
        PLAY_RINGTONE
}
