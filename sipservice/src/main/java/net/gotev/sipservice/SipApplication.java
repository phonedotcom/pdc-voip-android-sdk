/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package net.gotev.sipservice;

import static com.voismart.crypto.BuildConfig.VERSION_NAME;
import static net.gotev.sipservice.SharedPreferenceConstant.APPLICATION_ID;
import static net.gotev.sipservice.SharedPreferenceConstant.BUNDLE_ID;
import static net.gotev.sipservice.SharedPreferenceConstant.DEVICE_INFO;
import static net.gotev.sipservice.SharedPreferenceConstant.DEVICE_TYPE;
import static net.gotev.sipservice.SharedPreferenceConstant.DOMAIN_NAME;
import static net.gotev.sipservice.SharedPreferenceConstant.LOGS_FILE_NAME;
import static net.gotev.sipservice.SharedPreferenceConstant.LOGS_FOLDER_PATH;
import static net.gotev.sipservice.SharedPreferenceConstant.NOTIFICATION_BODY;
import static net.gotev.sipservice.SharedPreferenceConstant.NOTIFICATION_CONTENT_TITLE;
import static net.gotev.sipservice.SharedPreferenceConstant.NOTIFICATION_ICON;
import static net.gotev.sipservice.SharedPreferenceConstant.PORT;
import static net.gotev.sipservice.SharedPreferenceConstant.PROTOCOL_NAME;
import static net.gotev.sipservice.SharedPreferenceConstant.PUSH_TOKEN;
import static net.gotev.sipservice.SharedPreferenceConstant.SECURE_PORT;
import static net.gotev.sipservice.SharedPreferenceConstant.SECURE_PROTOCOL_NAME;
import static net.gotev.sipservice.SharedPreferenceConstant.SIP_PASSWORD;
import static net.gotev.sipservice.SharedPreferenceConstant.SIP_USER_NAME;
import static net.gotev.sipservice.SharedPreferenceConstant.VOIP_ID;
import static net.gotev.sipservice.SharedPreferenceConstant.VOIP_PHONE_ID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.pjsip.pjsua2.SipHeader;
import org.pjsip.pjsua2.SipHeaderVector;


/**
 * SipApplication class is used to save information to initialize library.
 */
public final class SipApplication {

    public static final String TAG = SipApplication.class.getSimpleName();

    private SipApplication(){}

    /**
     * This method is used to save information for push notification.
     *
     * @param pushToken     pushToken
     * @param versionName   versionName
     * @param bundleID      bundleID
     * @param deviceInfo    deviceInfo
     * @param applicationID applicationID
     * @param deviceType    deviceType
     * @param voipId        voipId
     * @param voipPhoneID   voipPhoneID
     * @param context       Android context needed
     */
    public static void saveInformationForPush(String pushToken, String versionName, String bundleID,
                                              String deviceInfo, String applicationID, String deviceType,
                                              String voipId, String voipPhoneID, Context context) {
        setPushToken(pushToken, context);
        setVersionName(versionName, context);
        setBundleID(bundleID, context);
        setDeviceInfo(deviceInfo, context);
        setApplicationID(applicationID, context);
        setHdeviceType(deviceType, context);
        sethVoipID(voipId, context);
        sethVoipPhoneID(voipPhoneID, context);
    }


    /**
     * This method is used to save information for library initialization.
     *
     * @param sipUsername        sipUsername
     * @param sipPassword        sipPassword
     * @param domainName         domainName
     * @param port               port
     * @param securePort         securePort
     * @param secureProtocolName secureProtocolName
     * @param protocolName       protocolName
     * @param context            Android context needed
     */
    public static void saveInformationForSipLibraryInitialization(String sipUsername, String sipPassword,
                                                                  String domainName, int port,
                                                                  int securePort, String secureProtocolName,
                                                                  String protocolName, Context context) {
        setSipUsername(sipUsername, context);
        setSipPassword(sipPassword, context);
        setDomainName(domainName, context);
        setPort(port, context);
        setSecurePort(securePort, context);
        setSecureProtocolName(secureProtocolName, context);
        setProtocolName(protocolName, context);
    }


    /**
     * This method is used to save foreground notification information.
     *
     * @param notificationTitle    notification Title
     * @param notificationSubtitle notification Subtitle
     * @param notificationIcon     notification icon
     * @param context              Android context needed
     */
    public static void saveInformationForForegroundServiceNotification(String notificationTitle,
                                                                       String notificationSubtitle,
                                                                       int notificationIcon,
                                                                       Context context) {
        setNotificationBody(notificationTitle, context);
        setNotificationContentTitle(notificationSubtitle, context);
        setNotificationIcon(notificationIcon, context);
    }


    /**
     * This method is used to get sipUsername.
     *
     * @param context Android context needed
     * @return sipUsername
     */
    public static String getSipUsername(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SIP_USER_NAME);
    }


    /**
     * This method is used to set sipUsername.
     *
     * @param sipUsername sipUsername
     * @param context     Android context needed
     */
    public static void setSipUsername(String sipUsername, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SIP_USER_NAME, sipUsername);
    }


    /**
     * This method is used to get sip password.
     *
     * @param context Android context needed
     * @return sip password
     */
    public static String getSipPassword(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SIP_PASSWORD);
    }


    /**
     * This method is used to set sip password.
     *
     * @param sipPassword sip password
     * @param context     Android context needed
     */
    public static void setSipPassword(String sipPassword, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SIP_PASSWORD, sipPassword);
    }


    /**
     * This method is used to get domain name.
     *
     * @param context Android context needed
     * @return domain name
     */
    public static String getDomainName(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(DOMAIN_NAME);
    }


    /**
     * This method is used to set notification domain name.
     *
     * @param domainName domain name
     * @param context    Android context needed
     */
    public static void setDomainName(String domainName, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(DOMAIN_NAME, domainName);
    }


    /**
     * This method is used to get port.
     *
     * @param context Android context needed
     * @return port
     */
    public static int getPort(Context context) {
        return SharedPreferencesHelper.getInstance(context).getIntSharedPreference(PORT);
    }


    /**
     * This method is used to set port.
     *
     * @param port    port
     * @param context Android context needed
     */
    public static void setPort(int port, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(PORT, port);
    }


    /**
     * This method is used to get securePort.
     *
     * @param context Android context needed
     * @return securePort
     */
    public static int getSecurePort(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getIntSharedPreference(SECURE_PORT);
    }


    /**
     * This method is used to set securePort.
     *
     * @param securePort securePort
     * @param context    Android context needed
     */
    public static void setSecurePort(int securePort, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SECURE_PORT, securePort);
    }


    /**
     * This method is used to get secureProtocolName.
     *
     * @param context Android context needed
     * @return secureProtocolName
     */
    public static String getSecureProtocolName(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(SECURE_PROTOCOL_NAME);
    }


    /**
     * This method is used to set secureProtocolName.
     *
     * @param secureProtocolName secureProtocolName
     * @param context            Android context needed
     */
    public static void setSecureProtocolName(String secureProtocolName, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(SECURE_PROTOCOL_NAME, secureProtocolName);
    }


    /**
     * This method is used to get protocolName.
     *
     * @param context Android context needed
     * @return protocolName
     */
    public static String getProtocolName(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(PROTOCOL_NAME);
    }


    /**
     * This method is used to set protocolName.
     *
     * @param protocolName protocolName
     * @param context      Android context needed
     */
    public static void setProtocolName(String protocolName, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(PROTOCOL_NAME, protocolName);
    }


    /**
     * This method is used to get pushToken.
     *
     * @param context Android context needed
     * @return pushToken
     */
    public static String getPushToken(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(PUSH_TOKEN);
    }


    /**
     * This method is used to set pushToken.
     *
     * @param pushToken pushToken
     * @param context   Android context needed
     */
    public static void setPushToken(String pushToken, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(PUSH_TOKEN, pushToken);
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
                .getStringSharedPreference(BUNDLE_ID);
    }


    /**
     * This method is used to set bundle id.
     *
     * @param bundleID bundle id
     * @param context  Android context needed
     */
    private static void setBundleID(String bundleID, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(BUNDLE_ID, bundleID);
    }


    /**
     * This method is used to get device info.
     *
     * @param context Android context needed
     * @return device info
     */
    public static String getDeviceInfo(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(DEVICE_INFO);
    }


    /**
     * This method is used to set device info.
     *
     * @param deviceInfo device info
     * @param context    Android context needed
     */
    private static void setDeviceInfo(String deviceInfo, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(DEVICE_INFO, deviceInfo);
    }


    /**
     * This method is used to get application id.
     *
     * @param context Android context needed
     * @return application id.
     */
    public static String getApplicationID(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(APPLICATION_ID);
    }


    /**
     * This method is used to set application id.
     *
     * @param applicationID application id
     * @param context       Android context needed
     */
    private static void setApplicationID(String applicationID, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(APPLICATION_ID, applicationID);
    }


    /**
     * This method is used to get device type.
     *
     * @param context Android context needed
     * @return device type
     */
    public static String getHdeviceType(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(DEVICE_TYPE);
    }


    /**
     * This method is used to set device type.
     *
     * @param hDeviceType device type
     * @param context     Android context needed
     */
    public static void setHdeviceType(String hDeviceType, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(DEVICE_TYPE, hDeviceType);
    }


    /**
     * This method is used to get voip id
     *
     * @param context Android context needed
     * @return voip id
     */
    public static String gethVoipID(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(VOIP_ID);
    }


    /**
     * This method is used to set voip id
     *
     * @param hVoipID voip id
     * @param context Android context needed
     */
    public static void sethVoipID(String hVoipID, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(VOIP_ID, hVoipID);
    }


    /**
     * This method is used to get voip phone id.
     *
     * @param context Android context needed
     * @return voip phone id
     */
    public static String gethVoipPhoneID(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(VOIP_PHONE_ID);
    }

    /**
     * This method is used to set voip phone id.
     *
     * @param hVoipPhoneID voip phone id
     * @param context      Android context needed
     */
    public static void sethVoipPhoneID(String hVoipPhoneID, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(VOIP_PHONE_ID, hVoipPhoneID);
    }


    /**
     * This method is used to get notification message.
     *
     * @param context Android context needed
     * @return notification message
     */
    public static String getNotificationBody(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(NOTIFICATION_BODY);
    }


    /**
     * This method is used to set notification message.
     *
     * @param notificationTitle message
     * @param context           Android context needed
     */
    public static void setNotificationBody(String notificationTitle, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(NOTIFICATION_BODY, notificationTitle);
    }


    /**
     * This method is used to get notification title.
     *
     * @param context Android context needed
     * @return notification title
     */
    public static String getNotificationContentTitle(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(NOTIFICATION_CONTENT_TITLE);
    }


    /**
     * This method is used to set notification title.
     *
     * @param notificationSubtitle title
     * @param context              Android context needed
     */
    public static void setNotificationContentTitle(String notificationSubtitle, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(NOTIFICATION_CONTENT_TITLE, notificationSubtitle);
    }


    /**
     * This method is used to get notification icon.
     *
     * @param context Android context needed
     * @return notification icon
     */
    public static int getNotificationIcon(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getIntSharedPreference(NOTIFICATION_ICON);
    }


    /**
     * This method is used to set notification icon.
     *
     * @param notificationIcon icon
     * @param context          Android context needed
     */
    public static void setNotificationIcon(int notificationIcon, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(NOTIFICATION_ICON, notificationIcon);
    }


    /**
     * This method is used to set log file name and path to sdk
     *
     * @param fileName filename for saving logs
     * @param context  Android context needed
     */
    public static void setLogFilesPathInformation(String fileName, Context context) {
        SharedPreferencesHelper.getInstance(context)
                .putInSharedPreference(LOGS_FILE_NAME, fileName);
    }


    /**
     * This method is used to retrieve log file directory.
     *
     * @param context Android context needed
     * @return path of log file directory
     */
    public static String getLogFilesFolderPath(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(LOGS_FOLDER_PATH);
    }


    /**
     * This method is used to retrieve log file path.
     *
     * @param context Android context needed
     * @return path of log file
     */
    public static String getLogFilePath(Context context) {
        return SharedPreferencesHelper.getInstance(context)
                .getStringSharedPreference(LOGS_FILE_NAME);
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
