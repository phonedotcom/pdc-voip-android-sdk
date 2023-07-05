package net.gotev.sipservice

import android.content.Context
import net.gotev.sipservice.models.ConfigureFCMPushNotification
import net.gotev.sipservice.models.ConfigurePhoneServiceNotification
import net.gotev.sipservice.models.ConfigureSip

class PDCInitialize(
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

        fun build(context: Context): PDCInitialize {
            this.context = context
            if (configurePushNotification != null) {
                SipServiceCommand.saveInformationForPushRegistration(
                    configurePushNotification,
                    context
                )
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

            return PDCInitialize(this)
        }
    }

    fun initialize() {
        SipServiceCommand.setAccount(this.context)
    }
}