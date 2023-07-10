package com.phone.sip.models

data class ConfigurePhoneServiceNotification(
    var appName: String,
    var notificationMessage: String,
    var notificationIcon: Int
)