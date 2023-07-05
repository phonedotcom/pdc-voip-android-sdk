package net.gotev.sipservice

import android.content.Context
import net.gotev.sipservice.model.FCMRegistrationDetails

class FCMRegistrationBuilder {

    private var context: Context? = null
    private var pushToken: String = ""
    private var versionName: String = ""
    private var bundleID: String = ""
    private var deviceInfo: String = ""
    private var applicationID: String = ""
    private var deviceType: String = ""
    private var voipId: String = ""
    private var voipPhoneID: String = ""

    fun build(): FCMRegistrationDetails {
        return FCMRegistrationDetails(
            context!!,
            pushToken,
            versionName,
            bundleID,
            deviceInfo,
            applicationID,
            deviceType,
            voipId,
            voipPhoneID
        )
    }

    fun setContext(context: Context): FCMRegistrationBuilder {
        this.context = context
        return this
    }

    fun setPushToken(pushToken: String): FCMRegistrationBuilder {
        this.pushToken = pushToken
        return this
    }

    fun setVersionName(versionName: String): FCMRegistrationBuilder {
        this.versionName = versionName
        return this
    }

    fun setBundleID(bundleID: String): FCMRegistrationBuilder {
        this.bundleID = bundleID
        return this
    }

    fun setDeviceInfo(deviceInfo: String): FCMRegistrationBuilder {
        this.deviceInfo = deviceInfo
        return this
    }

    fun setApplicationID(applicationID: String): FCMRegistrationBuilder {
        this.applicationID = applicationID
        return this
    }

    fun setDeviceType(deviceType: String): FCMRegistrationBuilder {
        this.deviceType = deviceType
        return this
    }

    fun setVoipId(voipId: String): FCMRegistrationBuilder {
        this.voipId = voipId
        return this
    }

    fun setVoipPhoneID(voipPhoneID: String): FCMRegistrationBuilder {
        this.voipPhoneID = voipPhoneID
        return this
    }

}