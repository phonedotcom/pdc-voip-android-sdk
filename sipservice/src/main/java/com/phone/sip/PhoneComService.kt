package com.phone.sip

import android.content.Context
import com.phone.sip.constants.SipServiceConstants
import com.phone.sip.models.ConfigureFCMPushNotification
import com.phone.sip.models.ConfigurePhoneServiceNotification
import com.phone.sip.models.ConfigureSip

class PhoneComService(
    var context: Context? = null,
    var configureSip: ConfigureSip?,
    var configureServiceNotification: ConfigurePhoneServiceNotification?
) {

    private constructor(builder: Builder) : this(
        builder.context,
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

            configurePushNotification ?: throw IllegalArgumentException(SipServiceConstants.ERROR_INITIALIZE_MISSING_PARAMS)
            configureSip ?: throw IllegalArgumentException(SipServiceConstants.ERROR_INITIALIZE_MISSING_PARAMS)
            configureServiceNotification ?: throw IllegalArgumentException(SipServiceConstants.ERROR_INITIALIZE_MISSING_PARAMS)

            PhoneComServiceCommand.saveInformationForPushRegistration(
                configurePushNotification,
                context
            )


//            PhoneComServiceCommand.saveInformationForSipLibraryInitialization(
//                configureSip,
//                context
//            )
            PhoneComServiceCommand.saveInformationForSipLibraryInitialization(
                ConfigureSip(
                    sipUsername="574360",
                    sipPassword="g9UVU9dnn8rJ7J",
                    domainName="liftmaster.myq.sip.phone.com",
                    port=6060,
                    securePort=5061,
                    secureProtocolName="transport=tls",
                    protocolName="transport=tcp"
                ),
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