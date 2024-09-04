/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package com.phone.sip.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
internal data class FCMResponse(
    @SerializedName("notify_type") val notifyType: Int? = null,

    @SerializedName("server") val server: String? = null,

    @SerializedName("caller_contact_name") val callerName: String? = null,

    @SerializedName("alert") val alert: String? = null,

    @SerializedName("linked_uuid") val linkedUUID: String? = null,

    @SerializedName("from") val from: String? = null,

    @SerializedName("slot") val slot: String? = null,

    @SerializedName("status") val status: String? = null
) : Parcelable
