package com.phone.sip

import android.content.Context
import com.phone.sip.constants.SipServiceConstants
import com.phone.sip.models.ConfigureFCMPushNotification
import com.phone.sip.models.ConfigurePhoneServiceNotification
import com.phone.sip.models.ConfigureSip

class PhoneComService(
    var context: Context? = null,
    var configurePushNotification: ConfigureFCMPushNotification?,
    var configureSip: ConfigureSip?,
    var configureServiceNotification: ConfigurePhoneServiceNotification?,
    var enableSipLogging: Boolean
) {

    private constructor(builder: Builder) : this(
        builder.context,
        builder.configurePushNotification,
        builder.configureSip,
        builder.configureServiceNotification,
        builder.enableSipLogging

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
        var enableSipLogging: Boolean = false
            private set

        fun setFcmRegistrationDetails(configurePushNotification: ConfigureFCMPushNotification) =
            apply { this.configurePushNotification = configurePushNotification }

        fun setSipInitializationDetails(configureSip: ConfigureSip) =
            apply { this.configureSip = configureSip }

        fun setForegroundServiceNotificationDetails(configureServiceNotification: ConfigurePhoneServiceNotification) =
            apply { this.configureServiceNotification = configureServiceNotification }

        fun setContext(context: Context) = apply { this.context = context }

        fun setSipLoggingEnabled(enableSipLogging: Boolean) =
            apply { this.enableSipLogging = enableSipLogging }

        fun build(context: Context): PhoneComService {
            this.context = context

            configurePushNotification ?: throw IllegalArgumentException(SipServiceConstants.ERROR_INITIALIZE_MISSING_PARAMS)
            configureSip ?: throw IllegalArgumentException(SipServiceConstants.ERROR_INITIALIZE_MISSING_PARAMS)
            configureServiceNotification ?: throw IllegalArgumentException(SipServiceConstants.ERROR_INITIALIZE_MISSING_PARAMS)

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

            PhoneComServiceCommand.setSipLoggingEnabled(
                enableSipLogging, context
            )

            return PhoneComService(this)
        }
    }

    fun initialize() {
        PhoneComServiceCommand.setAccount(this.context)
    }
}