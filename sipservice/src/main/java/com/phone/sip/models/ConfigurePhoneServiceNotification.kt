package com.phone.sip.models

import org.jetbrains.annotations.NotNull

data class ConfigurePhoneServiceNotification(
    @NotNull
    var notificationTitle: String,
    @NotNull
    var notificationMessage: String,
    @NotNull
    var notificationIcon: Int
)