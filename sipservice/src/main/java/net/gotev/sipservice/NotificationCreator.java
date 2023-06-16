/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package net.gotev.sipservice;

import static net.gotev.sipservice.SipServiceConstants.SERVICE_NOTIFICATION_CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public final class NotificationCreator {

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
        if (notificationBody == null) {
            notificationBody = SipApplication.getNotificationBody(context);
        }

        Intent resultIntent = new Intent();
        final BroadcastEventEmitter mBroadcastEmitter = new BroadcastEventEmitter(context);
        resultIntent.setAction(BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.INCOMING_CALL_NOTIFICATION_CLICK));
        resultIntent.putExtra(SipServiceConstants.INTENT_HANDLED, true);
        resultIntent.putExtra(SipServiceConstants.PARAM_IS_CALL, isCall);
        resultIntent = mBroadcastEmitter.getExplicitIntent(resultIntent);

        PendingIntent resultPendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

        NotificationCompat.Builder mBuilder;
        String channelId = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ? SERVICE_NOTIFICATION_CHANNEL_ID : "";
        mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(SipApplication.getNotificationIcon(context))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(SipApplication.getNotificationContentTitle(context))
                .setContentText(notificationBody);
        mBuilder.setContentIntent(resultPendingIntent);

        return mBuilder.build();
    }

    static Notification createForegroundServiceNotification(
            Context context, String callName
    ) {
        Intent resultIntent = new Intent();
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        String channelId = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ? SipServiceConstants.SERVICE_NOTIFICATION_CHANNEL_ID : "";
//        String channelId = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ? SipServiceConstants.GENERIC_PDC_VOIP_NOTIFICATION_CHANNEL : "";
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setContentText(callName);
        mBuilder.setContentIntent(resultPendingIntent);
        return mBuilder.build();
    }
}
