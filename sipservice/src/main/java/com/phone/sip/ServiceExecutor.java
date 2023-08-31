/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */

package com.phone.sip;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;



/**
 * ServiceExecutor class is the helper class for communicating with the {@link SipService}
 * This class is used by {@link PhoneComServiceCommand} for passing information
 * to perform operation by service.
 */
abstract class ServiceExecutor {

    private static final String TAG = ServiceExecutor.class.getSimpleName();

    /**
     * Method for passing information about the action to be performed by {@link SipService}<br>
     * It checks by calling {@link #isServiceRunningInForeground(Context, Class)} to check if
     * service is in foreground or not.<br>
     * If service is in foreground it calls {@link Context#startService(Intent)}
     * If service is not in foreground then it starts the service by calling
     * {@link ContextCompat#startForegroundService(Context, Intent)}
     *
     * @param context Android context for communicating with service
     * @param intent  bundle containing information for the type of operation to be performed
     *                bundle is passed to the service and based on that service perform the needed action.
     *                This bundle also contains the information required for performing the operation
     */
    public static synchronized void executeSipServiceAction(Context context, Intent intent) {
        Logger.debug(TAG, "alpha17 debug -> executeSipServiceAction()");
        intent.setComponent(new ComponentName(context, SipService.class));
        try {
            if (isServiceRunningInForeground(context, SipService.class)) {
                Logger.debug(TAG, "alpha17 debug -> executeSipServiceAction() -> if");
                context.startService(intent);
            } else {
                Logger.debug(TAG, "alpha17 debug -> executeSipServiceAction() -> else");
                ContextCompat.startForegroundService(context, intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for checking whether the service is running in foreground or not.
     * This method is used by {@link ServiceExecutor#executeSipServiceAction(Context, Intent)}
     *
     * @param context      Android context
     * @param serviceClass Service class name
     * @return boolean true if the service has asked to run as a foreground process  else false
     */
    private static boolean isServiceRunningInForeground(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }
}
