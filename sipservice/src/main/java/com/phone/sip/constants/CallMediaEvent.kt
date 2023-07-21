/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package com.phone.sip.constants

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.pjsip.pjsua2.pjmedia_event_type.*


@Parcelize
enum class CallMediaEvent(val code: Int, private val desc: String) : Parcelable {

    NONE(PJMEDIA_EVENT_NONE, "No event"),
    FORMAT_CHANGED(PJMEDIA_EVENT_FMT_CHANGED, "Media format has changed event"),
    WINDOW_CLOSING(PJMEDIA_EVENT_WND_CLOSING, "Video window is being closed"),
    WINDOW_CLOSED(PJMEDIA_EVENT_WND_CLOSED, "Video window has been closed event"),
    WINDOW_RESIZED(PJMEDIA_EVENT_WND_RESIZED, "Video window has been resized event"),
    MOUSE_BUTTON_DOWN(PJMEDIA_EVENT_MOUSE_BTN_DOWN, "Mouse button has been pressed event"),
    KEYFRAME_FOUND(PJMEDIA_EVENT_KEYFRAME_FOUND, "Video keyframe has just been decoded event"),
    KEYFRAME_MISSING(PJMEDIA_EVENT_KEYFRAME_MISSING, "Video decoding error due to missing keyframe event"),
    ORIENTATION_CHANGED(PJMEDIA_EVENT_ORIENT_CHANGED, "Video orientation has been changed event"),
    RTCP_FB(PJMEDIA_EVENT_RX_RTCP_FB, "RTCP-FB has been received"),
    AUDIO_ERROR(PJMEDIA_EVENT_AUD_DEV_ERROR, "Audio device stopped on error"),
    VIDEO_ERROR(PJMEDIA_EVENT_VID_DEV_ERROR, "Video device stopped on error"),
    TRANSPORT_ERROR(PJMEDIA_EVENT_MEDIA_TP_ERR, "Transport media error");

    companion object {
        private val map = HashMap<Int, CallMediaEvent>()

        init {
            for (pageType in values()) {
                map[pageType.code] = pageType
            }
        }

        fun valueOf(pageType: Int): CallMediaEvent? {
            return map[pageType] as CallMediaEvent?
        }
    }

    override fun toString(): String {
        return desc;
    }
}
