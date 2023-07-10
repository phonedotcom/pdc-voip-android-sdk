package com.phone.sip.models

data class ConfigureFCMPushNotification(
    var pushToken: String,
    var versionName: String,
    var bundleID: String,
    var deviceInfo: String,
    var applicationID: String,
    var deviceType: String,
    var voipId: String,
    var voipPhoneID: String
)