package net.gotev.sipservice

import android.content.Context

class FCMRegistration(
    var context: Context?,
    var pushToken: String,
    var versionName: String,
    var bundleID: String,
    var deviceInfo: String,
    var applicationID: String,
    var deviceType: String,
    var voipId: String,
    var voipPhoneID: String
) {

    private constructor(builder: Builder) : this(
        builder.context,
        builder.pushToken,
        builder.versionName,
        builder.bundleID,
        builder.deviceInfo,
        builder.applicationID,
        builder.deviceType,
        builder.voipId,
        builder.voipPhoneID
    )

    class Builder {
        var context: Context? = null
            private set
        var pushToken: String = ""
            private set
        var versionName: String = ""
            private set
        var bundleID: String = ""
            private set
        var deviceInfo: String = ""
            private set
        var applicationID: String = ""
            private set
        var deviceType: String = ""
            private set
        var voipId: String = ""
            private set
        var voipPhoneID: String = ""
            private set

        fun build() = FCMRegistration(this)

        fun context(context: Context) = apply { this.context = context }

        fun pushToken(pushToken: String) = apply { this.pushToken = pushToken }

        fun versionName(versionName: String) = apply { this.versionName = versionName }

        fun bundleID(bundleID: String) = apply { this.bundleID = bundleID }

        fun deviceInfo(deviceInfo: String) = apply { this.deviceInfo = deviceInfo }

        fun applicationID(applicationID: String) = apply { this.applicationID = applicationID }

        fun deviceType(deviceType: String) = apply { this.deviceType = deviceType }

        fun voipId(voipId: String) = apply { this.voipId = voipId }

        fun voipPhoneID(voipPhoneID: String) = apply { this.voipPhoneID = voipPhoneID }
    }
}