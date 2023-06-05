package net.gotev.sipservice;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SipUtility class is the utility class for sip related tasks.
 *
 * @author rajantalwar
 * @version 1.0
 * @since 2019-12-24.
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
        String format = String.format("sip:%s@%s:%d;%s", username, domainName, getPortNumber(context), protocolName);

        return format;
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
        return SharedPreferencesHelper.getInstance(context).isSecureProtocol(context) ? secureProtocolName : protocolName;
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
        return SharedPreferencesHelper.getInstance(context).isSecureProtocol(context) ? securedPort : port;
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
            AssetFileDescriptor afd = ctx.getAssets().openFd(fileName);
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

}
