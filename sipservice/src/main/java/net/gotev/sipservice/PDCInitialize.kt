package net.gotev.sipservice

import android.content.Context
import net.gotev.sipservice.model.FCMRegistrationDetails
import net.gotev.sipservice.model.ForegroundServiceNotificationDetails
import net.gotev.sipservice.model.SipInitializationDetails

class PDCInitialize {

    fun initialize(context: Context) {
        SipServiceCommand.setAccount(context)
    }

    fun setFcmRegistrationDetails(fcmRegistrationDetails: FCMRegistrationDetails) : PDCInitialize{
        SipServiceCommand.saveInformationForPushRegistration(fcmRegistrationDetails)
        return this
    }
    fun setSipInitializationDetails(sipInitializationDetails: SipInitializationDetails) : PDCInitialize{
        SipServiceCommand.saveInformationForSipLibraryInitialization(sipInitializationDetails)
        return this
    }
    fun setForegroundServiceNotificationDetails(foregroundServiceNotificationDetails: ForegroundServiceNotificationDetails) : PDCInitialize{
        SipServiceCommand.saveInformationForForegroundServiceNotification(foregroundServiceNotificationDetails)
        return this
    }
}