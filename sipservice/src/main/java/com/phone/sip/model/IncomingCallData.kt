/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package com.phone.sip.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 *
 */
@Parcelize
data class IncomingCallData(
    val from: String,
    val callerName: String,
    val linkedUUID: String,
    val notificationId: Int,
    val isVideo: Boolean
) : Parcelable {

    override fun toString(): String {
        return "IncomingCallData(from='$from', callerName='$callerName', linkedUUID='$linkedUUID', notificationId=$notificationId, isVideo=$isVideo)"
    }
}
