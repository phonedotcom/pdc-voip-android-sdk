package com.phone.sip.models

import org.jetbrains.annotations.NotNull

data class ConfigurePhoneServiceNotification(
    @NotNull
    var appName: String,
    @NotNull
    var notificationMessage: String,
    @NotNull
    var notificationIcon: Int
)