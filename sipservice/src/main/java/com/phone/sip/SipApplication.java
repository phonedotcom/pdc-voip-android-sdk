/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package com.phone.sip;

import static com.voismart.crypto.BuildConfig.VERSION_NAME;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.phone.sip.models.ConfigureFCMPushNotification;
import com.phone.sip.models.ConfigurePhoneServiceNotification;
import com.phone.sip.models.ConfigureSip;

import org.pjsip.pjsua2.SipHeader;
import org.pjsip.pjsua2.SipHeaderVector;


/**
 * SipApplication class is used to save information to initialize library.
 */
public final class SipApplication {

    public static final String TAG = SipApplication.class.getSimpleName();

    private SipApplication() {
    }

    /**
     * This method is used to save information for push notification.
     *
     * @param fcmRegistration FCMRegistration
     */
    public static void saveInformationForPush(ConfigureFCMPushNotification fcmRegistration, Context context) {
        setPushToken(fcmRegistration.getPushToken(), context);
        setVersionName(fcmRegistration.getVersionName(), context);
        setBundleID(fcmRegistration.getBundleID(), context);
        setDeviceInfo(fcmRegistration.getDeviceInfo(), context);
        setApplicationID(fcmRegistration.getApplicationID(), context);
        setHdeviceType(fcmRegistration.getDeviceType(), context);
        sethVoipID(fcmRegistration.getVoipId(), context);
        sethVoipPhoneID(fcmRegistration.getVoipPhoneID(), context);
    }


    /**
     * This method is used to save information for library initialization.
     *
     * @param sipInitialization SipInitialization
     */
    public static void saveInformationForSipLibraryInitialization(ConfigureSip sipInitialization, Context context) {
        setSipUsername(sipInitialization.getSipUsername(), context);
        setSipPassword(sipInitialization.getSipPassword(), context);
        setDomainName(sipInitialization.getDomainName(), context);
        setPort(sipInitialization.getPort(), context);
        setSecurePort(sipInitialization.getSecurePort(), context);
        setSecureProtocolName(sipInitialization.getSecureProtocolName(), context);
        setProtocolName(sipInitialization.getProtocolName(), context);
    }


    /**
     * This method is used to save foreground notification information.
     *
     * @param foregroundServiceNotification ForegroundServiceNotification
     */
    public static void saveInformationForForegroundServiceNotification(ConfigurePhoneServiceNotification foregroundServiceNotification, Context context) {
        setNotificationBody(foregroundServiceNotification.getAppName(), context);
        setNotificationContentTitle(foregroundServiceNotification.getNotificationMessage(), context);
        setNotificationIcon(foregroundServiceNotification.getNotificationIcon(), context);
    }


    /**
     * This method is used to get sipUsername.
     *
     * @param context Android context needed
     * @return sipUsername
     */
    public static String getSipUsername(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SharedPreferenceConstant.SIP_USER_NAME);
    }


    /**
     * This method is used to set sipUsername.
     *
     * @param sipUsername sipUsername
     * @param context     Android context needed
     */
    public static void setSipUsername(String sipUsername, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SharedPreferenceConstant.SIP_USER_NAME, sipUsername);
    }


    /**
     * This method is used to get sip password.
     *
     * @param context Android context needed
     * @return sip password
     */
    public static String getSipPassword(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SharedPreferenceConstant.SIP_PASSWORD);
    }


    /**
     * This method is used to set sip password.
     *
     * @param sipPassword sip password
     * @param context     Android context needed
     */
    public static void setSipPassword(String sipPassword, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SharedPreferenceConstant.SIP_PASSWORD, sipPassword);
    }


    /**
     * This method is used to get domain name.
     *
     * @param context Android context needed
     * @return domain name
     */
    public static String getDomainName(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SharedPreferenceConstant.DOMAIN_NAME);
    }


    /**
     * This method is used to set notification domain name.
     *
     * @param domainName domain name
     * @param context    Android context needed
     */
    public static void setDomainName(String domainName, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SharedPreferenceConstant.DOMAIN_NAME, domainName);
    }


    /**
     * This method is used to get port.
     *
     * @param context Android context needed
     * @return port
     */
    public static int getPort(Context context) {
        return SharedPreferencesHelper.getInstance(context).getIntSharedPreference(SharedPreferenceConstant.PORT);
    }


    /**
     * This method is used to set port.
     *
     * @param port    port
     * @param context Android context needed
     */
    public static void setPort(int port, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SharedPreferenceConstant.PORT, port);
    }


    /**
     * This method is used to get securePort.
     *
     * @param context Android context needed
     * @return securePort
     */
    public static int getSecurePort(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getIntSharedPreference(SharedPreferenceConstant.SECURE_PORT);
    }


    /**
     * This method is used to set securePort.
     *
     * @param securePort securePort
     * @param context    Android context needed
     */
    public static void setSecurePort(int securePort, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SharedPreferenceConstant.SECURE_PORT, securePort);
    }


    /**
     * This method is used to get secureProtocolName.
     *
     * @param context Android context needed
     * @return secureProtocolName
     */
    public static String getSecureProtocolName(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SharedPreferenceConstant.SECURE_PROTOCOL_NAME);
    }


    /**
     * This method is used to set secureProtocolName.
     *
     * @param secureProtocolName secureProtocolName
     * @param context            Android context needed
     */
    public static void setSecureProtocolName(String secureProtocolName, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SharedPreferenceConstant.SECURE_PROTOCOL_NAME, secureProtocolName);
    }


    /**
     * This method is used to get protocolName.
     *
     * @param context Android context needed
     * @return protocolName
     */
    public static String getProtocolName(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SharedPreferenceConstant.PROTOCOL_NAME);
    }


    /**
     * This method is used to set protocolName.
     *
     * @param protocolName protocolName
     * @param context      Android context needed
     */
    public static void setProtocolName(String protocolName, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SharedPreferenceConstant.PROTOCOL_NAME, protocolName);
    }


    /**
     * This method is used to get pushToken.
     *
     * @param context Android context needed
     * @return pushToken
     */
    public static String getPushToken(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SharedPreferenceConstant.PUSH_TOKEN);
    }


    /**
     * This method is used to set pushToken.
     *
     * @param pushToken pushToken
     * @param context   Android context needed
     */
    public static void setPushToken(String pushToken, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SharedPreferenceConstant.PUSH_TOKEN, pushToken);
    }


    /**
     * This method is used to get versionName.
     *
     * @param context Android context needed
     * @return versionName
     */
    public static String getVersionName(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(VERSION_NAME);
    }


    /**
     * This method is used to set versionName.
     *
     * @param versionName versionName
     * @param context     Android context needed
     */
    private static void setVersionName(String versionName, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(VERSION_NAME, versionName);
    }


    /**
     * This method is used to get bundle id.
     *
     * @param context Android context needed
     * @return bundle id
     */
    public static String getBundleID(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SharedPreferenceConstant.BUNDLE_ID);
    }


    /**
     * This method is used to set bundle id.
     *
     * @param bundleID bundle id
     * @param context  Android context needed
     */
    private static void setBundleID(String bundleID, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SharedPreferenceConstant.BUNDLE_ID, bundleID);
    }


    /**
     * This method is used to get device info.
     *
     * @param context Android context needed
     * @return device info
     */
    public static String getDeviceInfo(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SharedPreferenceConstant.DEVICE_INFO);
    }


    /**
     * This method is used to set device info.
     *
     * @param deviceInfo device info
     * @param context    Android context needed
     */
    private static void setDeviceInfo(String deviceInfo, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SharedPreferenceConstant.DEVICE_INFO, deviceInfo);
    }


    /**
     * This method is used to get application id.
     *
     * @param context Android context needed
     * @return application id.
     */
    public static String getApplicationID(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SharedPreferenceConstant.APPLICATION_ID);
    }


    /**
     * This method is used to set application id.
     *
     * @param applicationID application id
     * @param context       Android context needed
     */
    private static void setApplicationID(String applicationID, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SharedPreferenceConstant.APPLICATION_ID, applicationID);
    }


    /**
     * This method is used to get device type.
     *
     * @param context Android context needed
     * @return device type
     */
    public static String getHdeviceType(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SharedPreferenceConstant.DEVICE_TYPE);
    }


    /**
     * This method is used to set device type.
     *
     * @param hDeviceType device type
     * @param context     Android context needed
     */
    public static void setHdeviceType(String hDeviceType, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SharedPreferenceConstant.DEVICE_TYPE, hDeviceType);
    }


    /**
     * This method is used to get voip id
     *
     * @param context Android context needed
     * @return voip id
     */
    public static String gethVoipID(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SharedPreferenceConstant.VOIP_ID);
    }


    /**
     * This method is used to set voip id
     *
     * @param hVoipID voip id
     * @param context Android context needed
     */
    public static void sethVoipID(String hVoipID, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SharedPreferenceConstant.VOIP_ID, hVoipID);
    }


    /**
     * This method is used to get voip phone id.
     *
     * @param context Android context needed
     * @return voip phone id
     */
    public static String gethVoipPhoneID(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SharedPreferenceConstant.VOIP_PHONE_ID);
    }

    /**
     * This method is used to set voip phone id.
     *
     * @param hVoipPhoneID voip phone id
     * @param context      Android context needed
     */
    public static void sethVoipPhoneID(String hVoipPhoneID, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SharedPreferenceConstant.VOIP_PHONE_ID, hVoipPhoneID);
    }


    /**
     * This method is used to get notification message.
     *
     * @param context Android context needed
     * @return notification message
     */
    public static String getNotificationBody(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SharedPreferenceConstant.NOTIFICATION_BODY);
    }


    /**
     * This method is used to set notification message.
     *
     * @param notificationTitle message
     * @param context           Android context needed
     */
    public static void setNotificationBody(String notificationTitle, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SharedPreferenceConstant.NOTIFICATION_BODY, notificationTitle);
    }


    /**
     * This method is used to get notification title.
     *
     * @param context Android context needed
     * @return notification title
     */
    public static String getNotificationContentTitle(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SharedPreferenceConstant.NOTIFICATION_CONTENT_TITLE);
    }


    /**
     * This method is used to set notification title.
     *
     * @param notificationSubtitle title
     * @param context              Android context needed
     */
    public static void setNotificationContentTitle(String notificationSubtitle, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SharedPreferenceConstant.NOTIFICATION_CONTENT_TITLE, notificationSubtitle);
    }


    /**
     * This method is used to get notification icon.
     *
     * @param context Android context needed
     * @return notification icon
     */
    public static int getNotificationIcon(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getIntSharedPreference(SharedPreferenceConstant.NOTIFICATION_ICON);
    }


    /**
     * This method is used to set notification icon.
     *
     * @param notificationIcon icon
     * @param context          Android context needed
     */
    public static void setNotificationIcon(int notificationIcon, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SharedPreferenceConstant.NOTIFICATION_ICON, notificationIcon);
    }


    /**
     * This method is used to set log file name and path to sdk
     *
     * @param fileName filename for saving logs
     * @param context  Android context needed
     */
    public static void setLogFilesPathInformation(String fileName, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SharedPreferenceConstant.LOGS_FILE_NAME, fileName);
    }


    /**
     * This method is used to retrieve log file directory.
     *
     * @param context Android context needed
     * @return path of log file directory
     */
    public static String getLogFilesFolderPath(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SharedPreferenceConstant.LOGS_FOLDER_PATH);
    }


    /**
     * This method is used to retrieve log file path.
     *
     * @param context Android context needed
     * @return path of log file
     */
    public static String getLogFilePath(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SharedPreferenceConstant.LOGS_FILE_NAME);
    }

    public static SipHeaderVector getHeadersForPush(Context context) {
        SipHeader hdeviceToken = new SipHeader();
        hdeviceToken.setHName("X-Device-Token");
        hdeviceToken.setHValue(SipApplication.getPushToken(context));

        SipHeader hAppVersion = new SipHeader();
        hAppVersion.setHName("X-App-Version");
        hAppVersion.setHValue(SipApplication.getVersionName(context));

        SipHeader hbundleID = new SipHeader();
        hbundleID.setHName("X-Bundle-ID");
        hbundleID.setHValue(SipApplication.getBundleID(context));

        SipHeader hdeviceInfo = new SipHeader();
        hdeviceInfo.setHName("X-Device-Info");
        hdeviceInfo.setHValue(SipApplication.getDeviceInfo(context));

        SipHeader happlicationID = new SipHeader();
        happlicationID.setHName("X-Application-ID");
        happlicationID.setHValue(SipApplication.getApplicationID(context));

        SipHeader hDeviceType = new SipHeader();
        hDeviceType.setHName("X-Device-Type");
        hDeviceType.setHValue(SipApplication.getHdeviceType(context));

        SipHeader hVoIPID = new SipHeader();
        hVoIPID.setHName("X-VoIP-ID");
        hVoIPID.setHValue(SipApplication.gethVoipID(context));

        SipHeader hvoipPhoneID = new SipHeader();
        hvoipPhoneID.setHName("X-VoIP-Phone-ID");
        hvoipPhoneID.setHValue(SipApplication.gethVoipPhoneID(context));


        SipHeader hDebug = new SipHeader();
        hDebug.setHName("X-Debug-Mode");
        hDebug.setHValue("0");


        SipHeaderVector headerVector = new SipHeaderVector();
        headerVector.add(hdeviceToken);
        headerVector.add(hDeviceType);
        headerVector.add(hAppVersion);
        headerVector.add(hbundleID);
        headerVector.add(hdeviceInfo);
        headerVector.add(happlicationID);
        headerVector.add(hvoipPhoneID);
        headerVector.add(hVoIPID);
        headerVector.add(hDebug);

        return headerVector;
    }

    public static SipHeader getHeadersForUnregisterPush() {
        final SipHeader hLogoutHeader = new SipHeader();
        hLogoutHeader.setHName("X-Action");
        hLogoutHeader.setHValue("Logout");
        return hLogoutHeader;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    void createChannelId(
            Context context,
            String channelId,
            Boolean vibration,
            int importance,
            String channelName,
            Boolean badge
    ) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId,
                    channelName,
                    importance
            );
            notificationChannel.enableVibration(vibration);
            notificationChannel.setShowBadge(badge);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public static boolean isToAddHeadersForPushNotification(Context context) {
        if (
                StringUtility.validateString(SipApplication.getPushToken(context))
                        && StringUtility.validateString(SipApplication.getVersionName(context))
                        && StringUtility.validateString(SipApplication.getBundleID(context))
                        && StringUtility.validateString(SipApplication.getDeviceInfo(context))
                        && StringUtility.validateString(SipApplication.getApplicationID(context))
        ) {
            Logger.debug(TAG, "Headers are present");
            return true;
        }
        Logger.debug(TAG, "Headers are MISSING");
        return false;
    }

}
