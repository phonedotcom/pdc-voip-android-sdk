/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package com.phone.sip.constants

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
enum class CallEvent(val code: Int, val state: String) : Parcelable {
    CALL_INITIATED(0, "Call Initiated"),
    DIALING(1, "Dialing"),
    RINGING(2, "Ringing"),
    CONNECTING(3, "Connecting"),
    RECEIVING(4, "Received"),
    PLAY_RINGTONE(5, "Play Ringtone"),
    VIDEO_INITIATED(6, "Video Initiated"),
    ONGOING_CALL(7, "On Going"),
    DISCONNECTED(8, "Disconnected");

    override fun toString(): String {
        return state;
    }
}