package net.gotev.sipservice

import android.content.Context
import net.gotev.sipservice.models.ConfigureFCMPushNotification
import net.gotev.sipservice.models.ConfigurePhoneServiceNotification
import net.gotev.sipservice.models.ConfigureSip

class PDCInitialize {

    private var context: Context? = null
    private var configurePushNotification: ConfigureFCMPushNotification? = null
    private var configureSip: ConfigureSip? = null
    private var configureServiceNotification: ConfigurePhoneServiceNotification? = null

    fun initialize(context: Context): PDCInitialize {
        this.context = context
        if (configurePushNotification != null) {
            SipServiceCommand.saveInformationForPushRegistration(configurePushNotification, context)
        } else {
            throw Exception("Missing Push notification parameters.")
        }

        if (configureSip != null) {
            SipServiceCommand.saveInformationForSipLibraryInitialization(configureSip, context)
        } else {
            throw Exception("Missing SIP configuration parameters.")
        }

        if (configureServiceNotification != null) {
            SipServiceCommand.saveInformationForForegroundServiceNotification(
                configureServiceNotification, context
            )
        } else {
            throw Exception("Missing phone service notification parameters.")
        }

        return this
    }

    fun build() {
        if (context == null) {
            throw Exception("No context found, Please re-initialize library with valid context.")
        }
        SipServiceCommand.setAccount(context)
    }

    fun setFcmRegistrationDetails(configurePushNotification: ConfigureFCMPushNotification): PDCInitialize {
        this.configurePushNotification = configurePushNotification
        return this
    }

    fun setSipInitializationDetails(configureSip: ConfigureSip): PDCInitialize {
        this.configureSip = configureSip
        return this
    }

    fun setForegroundServiceNotificationDetails(configureServiceNotification: ConfigurePhoneServiceNotification): PDCInitialize {
        this.configureServiceNotification = configureServiceNotification
        return this
    }
}