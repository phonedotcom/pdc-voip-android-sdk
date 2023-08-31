/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package com.phone.sip

import android.app.Notification
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.phone.sip.constants.PhoneComServiceConstants.SERVICE_NOTIFICATION_CHANNEL_ID

class NotificationCreator private constructor() {
    init {
        throw UnsupportedOperationException("This is a creator class and cannot be instantiated")
    }

    companion object {
        val TAG: String = NotificationCreator::class.java.simpleName

        /**
         * Method for creating an ongoing call notification
         *
         * @param context Android context needed for talking to SDK service
         *
         * @apiNote  getApplicationInfo().loadLabel(getPackageManager()) provides app name, if no title is provided
         */
        fun createForegroundServiceNotification(
            context: Context?, notificationBody: String, isCall: Boolean
        ): Notification {
            return createForegroundServiceNotification(context, notificationBody)
        }

        /**
         * Method for creating an ongoing call notification
         *
         * @param context Android context needed for talking to SDK service
         */
        fun createForegroundServiceNotification(context: Context?, priority: Int): Notification {
            return createForegroundServiceNotification(context, "", priority)
        }
        /**
         * Method for creating an ongoing call notification
         *
         * @param context Android context needed for talking to SDK service
         * @param notificationBody Notification body
         * @param priority [NotificationCompat.PRIORITY_MAX] | [NotificationCompat.PRIORITY_MIN]
         * | [NotificationCompat.PRIORITY_DEFAULT]
         */
        @JvmOverloads
        fun createForegroundServiceNotification(
            context: Context?,
            notificationBody: String = "",
            priority: Int = NotificationCompat.PRIORITY_MAX
        ): Notification {
            var contentText = notificationBody
            Logger.debug(
                TAG, "alpha17 debug -> createForegroundServiceNotification(context, notificationBody, priority)"
            )
            if (!StringUtility.validate(contentText)) {
                contentText = SipApplication.getNotificationBody(context)
            }
            val channelId =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) SERVICE_NOTIFICATION_CHANNEL_ID else ""
            val mBuilder = NotificationCompat.Builder(context!!, channelId)
                .setSmallIcon(SipApplication.getNotificationIcon(context))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(priority)
                .setLights(Color.GREEN, 1000, 500)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(SipApplication.getNotificationContentTitle(context))
                .setAutoCancel(true)
                .setContentText(contentText)

            Logger.debug(
                TAG, """
     NotificationTitle: ${SipApplication.getNotificationContentTitle(context)}
     NotificationBody: $contentText
     NotificationPriority: $priority
     """.trimIndent()
            )
            return mBuilder.build()
        }
    }
}