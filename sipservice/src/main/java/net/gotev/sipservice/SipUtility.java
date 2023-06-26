/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package net.gotev.sipservice;

import static net.gotev.sipservice.SipServiceConstants.PARAM_INCOMING_FROM;
import static net.gotev.sipservice.SipServiceConstants.PARAM_INCOMING_LINKED_UUID;
import static net.gotev.sipservice.SipServiceConstants.PARAM_INCOMING_SERVER;
import static net.gotev.sipservice.SipServiceConstants.PARAM_INCOMING_SLOT;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SipUtility class is the utility class for sip related tasks.
 */
public class SipUtility {

    /**
     * This method is used to get uri of Sip user
     *
     * @param username username
     * @param context Android context needed
     * @return sip user uri
     */
    public static String getSipUserUri(String username, Context context) {
        String domainName = SipApplication.getDomainName(context);
        try {
            username = URLEncoder.encode(username, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String protocolName = getProtocolName(context);

        return String.format(
                Locale.getDefault(),
                "sip:%s@%s:%d;%s", username, domainName, getPortNumber(context), protocolName
        );
    }


    /**
     * This method is used to get protocol name.
     *
     * @param context Android context needed
     * @return protocol name
     */
    @NonNull
    private static String getProtocolName(Context context) {
        String secureProtocolName = SipApplication.getSecureProtocolName(context);
        String protocolName = SipApplication.getProtocolName(context);
        return SharedPreferencesHelper.getInstance(context).isSecureProtocol() ? secureProtocolName : protocolName;
    }


    /**
     * This method is used to get port number.
     *
     * @param context Android context needed
     * @return port number
     */
    private static int getPortNumber(Context context) {
        int port = SipApplication.getPort(context);
        int securedPort = SipApplication.getSecurePort(context);
        return SharedPreferencesHelper.getInstance(context).isSecureProtocol() ? securedPort : port;
    }


    /**
     * This method is used to get uri of domain.
     *
     * @param context Android context needed
     * @return uri of domain
     */
    public static String getDomainUri(Context context) {
        String protocolName = getProtocolName(context);
        String domainName = SipApplication.getDomainName(context);
        return String.format("sip:%s:%d;%s", domainName, getPortNumber(context), protocolName);
    }


    /**
     * This method is used to get time in seconds.
     *
     * @return current time in seconds
     */
    public static long timeInSeconds() {
        long millis = System.currentTimeMillis();
        return millis / 1000;
    }


    /**
     * This method is used to get uuid from the header message passed.
     *
     * @param header header message
     * @return uuid
     */
    public static String getUUIDFromHeader(String header) {
        String patternString = "X-Linked-UUID:(.*)(?:\\s.*)*";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(header);
        String uuid = "";
        if (matcher.find()) {
            try {
                uuid = matcher.group(1).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return uuid;
    }


    /**
     * This method is used to get name from the header message passed.
     *
     * @param header message
     * @return name
     */
    public static String getNameFromHeader(String header) {
        String patternString = "From:(.*)(?:\\s.*)*";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(header);
        String uuid = "";
        if (matcher.find()) {
            try {
                uuid = matcher.group(1).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return uuid;
    }


    /**
     * This method is used to play custom ringtone in device.
     *
     * @param fileName filename to play ringtone
     * @param ctx      context Android context needed
     */
    public static void playSound(String fileName, Context ctx) {
        MediaPlayer p = new MediaPlayer();
        try {
            final AssetFileDescriptor afd = ctx.getAssets().openFd(fileName);
            p.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            p.prepare();
            p.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This method is used to get username from the message passed.
     *
     * @param message message
     * @return username
     */
    public static String getUserName(String message) {
        if (message != null) {
            String name = "";
            //  String message = ":\"New extension\" <sip:525@72.1.47.164>;tag=sansay502590rdb9linkedUUID:5080e5a8-18d8-4275-a2f9-bf9537e49839";
            String[] strArray = message.split(":|\"|<");
            for (String splits : strArray) {
                name = splits;
                if (name != null && !name.trim().isEmpty())
                    break;
            }
            return name.trim();
        }
        return message;
    }


    public static IncomingCall createIncomingCallObject(Intent intent) {
        // assign caller name at this point to avoid multiple Utility calls
        final String number = intent.getStringExtra(PARAM_INCOMING_FROM);
        final String server = intent.getStringExtra(PARAM_INCOMING_SERVER);
        final String slot = intent.getStringExtra(PARAM_INCOMING_SLOT);
        final String linked_uid = intent.getStringExtra(PARAM_INCOMING_LINKED_UUID);

        String callerName = number;

        if (intent.hasExtra(SipServiceConstants.PARAM_DISPLAY_NAME)) {
            callerName = intent.getStringExtra(SipServiceConstants.PARAM_DISPLAY_NAME);
        }

        // create a call over here
        final IncomingCall incomingCall = new IncomingCall();
        incomingCall.setLinkedUUID(linked_uid);
        incomingCall.setServer(server);
        incomingCall.setSlot(slot);
        incomingCall.setNumber(number);

        incomingCall.setCallerNumber(callerName);
        incomingCall.setCallerName(callerName);
        incomingCall.setCallType(CallType.INCOMING);
        incomingCall.setTime(SipUtility.timeInSeconds());
        incomingCall.setState(CallState.INCOMING_CALL);
        return incomingCall;
    }

}
