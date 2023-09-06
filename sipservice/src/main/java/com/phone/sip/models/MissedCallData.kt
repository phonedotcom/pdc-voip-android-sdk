/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package com.phone.sip.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MissedCallData(val callerName: String, val linkedUUID: String, val notificationId: Int) :
    Parcelable {
    override fun toString(): String {
        return "MissedCallData(callerName='$callerName', linkedUUID='$linkedUUID', notificationId= '$notificationId')"
    }
}
