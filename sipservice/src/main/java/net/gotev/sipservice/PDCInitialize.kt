package net.gotev.sipservice

import android.content.Context

class PDCInitialize {

    fun initialize(context: Context) {
        SipServiceCommand.setAccount(context)
    }

    fun setFcmRegistrationDetails(fcmRegistrationDetails: FCMRegistration): PDCInitialize {
        SipServiceCommand.saveInformationForPushRegistration(fcmRegistrationDetails)
        return this
    }

    fun setSipInitializationDetails(sipInitialization: SipInitialization): PDCInitialize {
        SipServiceCommand.saveInformationForSipLibraryInitialization(sipInitialization)
        return this
    }

    fun setForegroundServiceNotificationDetails(foregroundServiceNotificationDetails: ForegroundServiceNotification): PDCInitialize {
        SipServiceCommand.saveInformationForForegroundServiceNotification(
            foregroundServiceNotificationDetails
        )
        return this
    }
}