package com.phone.sip

import android.content.Context
import com.phone.sip.models.ConfigureFCMPushNotification
import com.phone.sip.models.ConfigurePhoneServiceNotification
import com.phone.sip.models.ConfigureSip

class PhoneComService(
    var context: Context? = null,
    var configurePushNotification: ConfigureFCMPushNotification? = null,
    var configureSip: ConfigureSip? = null,
    var configureServiceNotification: ConfigurePhoneServiceNotification? = null
) {

    private constructor(builder: Builder) : this(
        builder.context,
        builder.configurePushNotification,
        builder.configureSip,
        builder.configureServiceNotification
    )

    class Builder {
        var context: Context? = null
            private set
        var configurePushNotification: ConfigureFCMPushNotification? = null
            private set
        var configureSip: ConfigureSip? = null
            private set
        var configureServiceNotification: ConfigurePhoneServiceNotification? = null
            private set

        fun setFcmRegistrationDetails(configurePushNotification: ConfigureFCMPushNotification) =
            apply { this.configurePushNotification = configurePushNotification }

        fun setSipInitializationDetails(configureSip: ConfigureSip) =
            apply { this.configureSip = configureSip }

        fun setForegroundServiceNotificationDetails(configureServiceNotification: ConfigurePhoneServiceNotification) =
            apply { this.configureServiceNotification = configureServiceNotification }

        fun setContext(context: Context) = apply { this.context = context }

        fun build(context: Context): PhoneComService {
            this.context = context
            if (configurePushNotification != null) {
                PhoneComServiceCommand.saveInformationForPushRegistration(
                    configurePushNotification,
                    context
                )
            } else {
                throw Exception("Missing Push notification parameters.")
            }

            if (configureSip != null) {
                PhoneComServiceCommand.saveInformationForSipLibraryInitialization(
                    configureSip,
                    context
                )
            } else {
                throw Exception("Missing SIP configuration parameters.")
            }

            if (configureServiceNotification != null) {
                PhoneComServiceCommand.saveInformationForForegroundServiceNotification(
                    configureServiceNotification, context
                )
            } else {
                throw Exception("Missing phone service notification parameters.")
            }

            return PhoneComService(this)
        }
    }

    fun initialize() {
        PhoneComServiceCommand.setAccount(this.context)
    }
}