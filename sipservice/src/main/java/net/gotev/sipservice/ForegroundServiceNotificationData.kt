package net.gotev.sipservice

import android.content.Context
import net.gotev.sipservice.model.ForegroundServiceNotificationDetails

class ForegroundServiceNotificationBuilder {

    private var context: Context? = null
    private var appName: String = ""
    private var notificationMessage: String = ""
    private var notificationIcon: Int = 0

    fun build(): ForegroundServiceNotificationDetails {
        return ForegroundServiceNotificationDetails(
            context!!,
            appName,
            notificationMessage,
            notificationIcon
        )
    }

    fun setContext(context: Context): ForegroundServiceNotificationBuilder {
        this.context = context
        return this
    }

    fun setAppName(appName: String): ForegroundServiceNotificationBuilder {
        this.appName = appName
        return this
    }

    fun setNotificationMessage(notificationMessage: String): ForegroundServiceNotificationBuilder {
        this.notificationMessage = notificationMessage
        return this
    }

    fun setNotificationIcon(notificationIcon: Int): ForegroundServiceNotificationBuilder {
        this.notificationIcon = notificationIcon
        return this
    }
}