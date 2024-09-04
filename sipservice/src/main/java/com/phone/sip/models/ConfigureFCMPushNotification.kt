/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package com.phone.sip.models

import org.jetbrains.annotations.NotNull

data class ConfigureFCMPushNotification(
    @NotNull
    var pushToken: String,
    @NotNull
    var versionName: String,
    @NotNull
    var bundleID: String,
    @NotNull
    var deviceInfo: String,
    @NotNull
    var applicationID: String,
    @NotNull
    var deviceType: String,
    @NotNull
    var voipId: String,
    @NotNull
    var voipPhoneID: String
)