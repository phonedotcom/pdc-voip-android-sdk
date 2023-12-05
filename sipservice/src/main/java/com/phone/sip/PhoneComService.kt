package com.phone.sip

import android.content.Context
import com.phone.sip.constants.SipServiceConstants
import com.phone.sip.models.ConfigureFCMPushNotification
import com.phone.sip.models.ConfigurePhoneServiceNotification
import com.phone.sip.models.ConfigureSip

class PhoneComService(
    var context: Context? = null,
    var configureSip: ConfigureSip?,
    var configureServiceNotification: ConfigurePhoneServiceNotification?,
    var enableSipConsoleLogging: Boolean,
    var enableSipFileLogging: Boolean,
    var logFilePath: String
) {

    private constructor(builder: Builder) : this(
        builder.context,
        builder.configureSip,
        builder.configureServiceNotification,
        builder.enableSipConsoleLogging,
        builder.enableSipFileLogging,
        builder.logFilePath
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
        var enableSipConsoleLogging: Boolean = false
            private set
        var enableSipFileLogging: Boolean = false
            private set
        var logFilePath: String = ""
            private set

        fun setFcmRegistrationDetails(configurePushNotification: ConfigureFCMPushNotification) =
            apply { this.configurePushNotification = configurePushNotification }

        fun setSipInitializationDetails(configureSip: ConfigureSip) =
            apply { this.configureSip = configureSip }

        fun setForegroundServiceNotificationDetails(configureServiceNotification: ConfigurePhoneServiceNotification) =
            apply { this.configureServiceNotification = configureServiceNotification }

        fun setContext(context: Context) = apply { this.context = context }

        fun setSipFileLoggingEnabled(enableSipFileLogging: Boolean, logFilePath: String) = apply {
            this.enableSipFileLogging = enableSipFileLogging
            this.logFilePath = logFilePath
        }

        fun setSipConsoleLoggingEnabled(enableSipConsoleLogging: Boolean) =
            apply { this.enableSipConsoleLogging = enableSipConsoleLogging }

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

            PhoneComServiceCommand.setSipFileLoggingEnabled(
                enableSipFileLogging, logFilePath, context
            )

            PhoneComServiceCommand.setSipConsoleLoggingEnabled(
                enableSipConsoleLogging, context
            )

            return PhoneComService(this)
        }
    }

    fun initialize() {
        PhoneComServiceCommand.setAccount(this.context)
    }
}