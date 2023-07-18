/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package com.phone.sip;

import static com.phone.sip.constants.PhoneComServiceConstants.SERVICE_NOTIFICATION_CHANNEL_ID;

import android.app.Notification;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public final class NotificationCreator {

    public static final String TAG = NotificationCreator.class.getSimpleName();

    private NotificationCreator(){
        throw new UnsupportedOperationException("This is a creator class and cannot be instantiated");
    }

    /**
     * Method for creating an ongoing call notification
     *
     * @param notificationBody notification body
     * @param isCall boolean if true there is an ongoing active call else there is no active call
     * @param context Android context needed for talking to SDK service
     *
     * @apiNote  getApplicationInfo().loadLabel(getPackageManager()) provides app name, if no title is provided
     */
    static Notification createForegroundServiceNotification(Context context, String notificationBody, boolean isCall) {
        return createForegroundServiceNotification(context, notificationBody);
    }

    /**
     * Method for creating an ongoing call notification
     *
     * @param context Android context needed for talking to SDK service
     */
    static Notification createForegroundServiceNotification(Context context) {
        return createForegroundServiceNotification(context, "");
    }

    /**
     * Method for creating an ongoing call notification
     *
     * @param notificationBody notification body
     * @param context Android context needed for talking to SDK service
     */
    static Notification createForegroundServiceNotification(
            Context context, String notificationBody
    ) {
        Logger.debug(TAG, "createForegroundServiceNotification(context, notificationBody)");
        if (!StringUtility.validate(notificationBody)) {
            notificationBody = SipApplication.getNotificationBody(context);
        }

        final String channelId = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ? SERVICE_NOTIFICATION_CHANNEL_ID : "";
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(SipApplication.getNotificationIcon(context))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(SipApplication.getNotificationContentTitle(context))
                .setAutoCancel(true)
                .setContentText(notificationBody);

        Logger.debug(TAG, "NotificationTitle: "+SipApplication.getNotificationContentTitle(context) + "\n" + "NotificationBody: "+notificationBody);

        return mBuilder.build();
    }
}
