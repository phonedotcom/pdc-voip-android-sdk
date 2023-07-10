/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package net.gotev.sipservice;

import static net.gotev.sipservice.constants.PhoneComServiceConstants.SERVICE_NOTIFICATION_CHANNEL_ID;

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
     */
    public static Notification createForegroundServiceNotification(Context context, String notificationBody, boolean isCall) {
        Logger.debug(TAG, "createForegroundServiceNotification(context, notificationBody, isCall)");
        if (notificationBody == null) {
            notificationBody = SipApplication.getNotificationBody(context);
        }

        final String channelId = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ? SERVICE_NOTIFICATION_CHANNEL_ID : "";
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(SipApplication.getNotificationIcon(context))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(SipApplication.getNotificationContentTitle(context))
                .setContentText(notificationBody);

        return mBuilder.build();
    }

    static Notification createForegroundServiceNotification(
            Context context, String callName
    ) {
        Logger.debug(TAG, "createForegroundServiceNotification(context, callName)");
        /*Intent resultIntent = new Intent();
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );*/
        String channelId = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ? SERVICE_NOTIFICATION_CHANNEL_ID : "";
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(SipApplication.getNotificationIcon(context))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(SipApplication.getNotificationContentTitle(context))
                .setContentText(callName);
        //mBuilder.setContentIntent(resultPendingIntent);
        return mBuilder.build();
    }
}
