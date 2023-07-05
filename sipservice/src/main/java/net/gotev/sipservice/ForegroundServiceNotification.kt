package net.gotev.sipservice

import android.content.Context

class ForegroundServiceNotification(
    var context: Context?,
    var appName: String,
    var notificationMessage: String,
    var notificationIcon: Int
) {
    private constructor(builder: Builder) : this(
        builder.context,
        builder.appName,
        builder.notificationMessage,
        builder.notificationIcon
    )

    class Builder {
        var context: Context? = null
            private set
        var appName: String = ""
            private set
        var notificationMessage: String = ""
            private set
        var notificationIcon: Int = 0
            private set

        fun build() = ForegroundServiceNotification(this)

        fun context(context: Context) = apply { this.context = context }

        fun appName(appName: String) = apply { this.appName = appName }

        fun notificationMessage(notificationMessage: String) =
            apply { this.notificationMessage = notificationMessage }

        fun notificationIcon(notificationIcon: Int) =
            apply { this.notificationIcon = notificationIcon }

    }
}