package net.gotev.sipservice.model

import android.content.Context

data class FCMRegistrationDetails(
    val context: Context,
    val pushToken: String,
    val versionName: String,
    val bundleID: String,
    val deviceInfo: String,
    val applicationID: String,
    val deviceType: String,
    val voipId: String,
    val voipPhoneID: String
)
