package com.phone.sip

import android.content.Context
import com.phone.sip.models.ConfigureFCMPushNotification
import com.phone.sip.models.ConfigurePhoneServiceNotification
import com.phone.sip.models.ConfigureSip

class PhoneComService(
    var context: Context? = null,
    var configurePushNotification: ConfigureFCMPushNotification,
    var configureSip: ConfigureSip,
    var configureServiceNotification: ConfigurePhoneServiceNotification
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
        lateinit var configurePushNotification: ConfigureFCMPushNotification
            private set
        lateinit var configureSip: ConfigureSip
            private set
        lateinit var configureServiceNotification: ConfigurePhoneServiceNotification
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
            PhoneComServiceCommand.saveInformationForPushRegistration(
                configurePushNotification,
                context
            )

            PhoneComServiceCommand.saveInformationForSipLibraryInitialization(
                configureSip,
                context
            )

            PhoneComServiceCommand.saveInformationForForegroundServiceNotification(
                configureServiceNotification, context
            )

            return PhoneComService(this)
        }
    }

    fun initialize() {
        PhoneComServiceCommand.setAccount(this.context)
    }
}