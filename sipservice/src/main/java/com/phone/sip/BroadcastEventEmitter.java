package com.phone.sip;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;

import com.phone.sip.constants.CallEvent;
import com.phone.sip.constants.CallMediaEvent;
import com.phone.sip.constants.InitializeStatus;
import com.phone.sip.constants.SipServiceConstants;
import com.phone.sip.models.IncomingCallData;
import com.phone.sip.models.MissedCallData;

import java.util.ArrayList;
import java.util.List;

/**
 * Emits the sip service broadcast intents.
 *
 * @author gotev (Aleksandar Gotev)
 */
public class BroadcastEventEmitter implements SipServiceConstants {

    public static final String TAG = BroadcastEventEmitter.class.getSimpleName();
    public static String NAMESPACE = "com.phone.voip";

    private final Context mContext;

    /**
     * Enumeration of the broadcast actions
     */
    public enum BroadcastAction {
        REGISTRATION,
        INCOMING_CALL,
        CALL_EVENT,
        CALL_MEDIA_STATE,
        OUTGOING_CALL,
        STACK_STATUS,
        CODEC_PRIORITIES,
        CODEC_PRIORITIES_SET_STATUS,
        MISSED_CALL,
        VIDEO_SIZE,
        CALL_STATS,
        CALL_RECONNECTION_STATE,
        SILENT_CALL_STATUS,
        NOTIFY_TLS_VERIFY_STATUS_FAILED,
        CALLBACK_REMOVE_ACCOUNT,
        INCOMING_CALL_NOTIFICATION_CLICK,
        ACCEPT_INCOMING_CALL_ACTION,
        END_SERVICE_ACTION,
        CALL_MEDIA_EVENT,
        CALLBACK_GENERIC_ERROR,
        INITIALIZE,
        HOLD_CALL,
        RESUME_CALL
    }

    public BroadcastEventEmitter(Context context) {
        mContext = context;
    }

    public static String getAction(BroadcastAction action) {
        return NAMESPACE + "." + action;
    }

    /**
     * Emit an incoming call broadcast intent.
     *
     * @param number Caller Number
     * @param server Server details
     * @param slot Slot provided
     * @param linkedUUID Linked UUID for call
     * @param callName Caller Name
     * @param isVideo Call supports Video or no
     */
   /* public void incomingCall
    (
            String number, String server,
            String slot, String linkedUUID, String callName,
            boolean isActiveCallPresent,
            boolean isVideo
    ) {
        final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastAction.INCOMING_CALL));
        //intent.putExtra(PARAM_ACCOUNT_ID, accountID);
        intent.putExtra(PARAM_INCOMING_FROM, number);
        intent.putExtra(PARAM_INCOMING_SERVER, server);
        intent.putExtra(PARAM_INCOMING_SLOT, slot);
        intent.putExtra(PARAM_INCOMING_LINKED_UUID, linkedUUID);
        intent.putExtra(PARAM_DISPLAY_NAME, callName);
        intent.putExtra(PARAM_ANY_ACTIVE_CALL, isActiveCallPresent);
        intent.putExtra(PARAM_IS_VIDEO, isVideo);

        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        mContext.sendBroadcast(intent);
    }*/

    /**
     * Emit an incoming call broadcast intent.
     *
     * @param incomingCallData {@link IncomingCallData}
     * @param isAnyActiveCall  Any active call present (true/false)
     */
    public void incomingCall(final IncomingCallData incomingCallData, boolean isAnyActiveCall) {
        final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastAction.INCOMING_CALL));
        intent.putExtra(PARAM_INCOMING_CALL_DATA, incomingCallData);
        intent.putExtra(PARAM_ANY_ACTIVE_CALL, isAnyActiveCall);

        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        mContext.sendBroadcast(intent);
    }

    /**
     * Emit an incoming call broadcast intent.
     *
     * @param accountID   call's account IdUri
     * @param callID      call ID number
     * @param displayName the display name of the remote party
     * @param remoteUri   the IdUri of the remote party
     * @param isVideo     whether the call has video or not
     */
    /*public void incomingCall(String accountID, int callID, String displayName, String remoteUri, boolean isVideo) {
        final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastAction.INCOMING_CALL));
        intent.putExtra(PARAM_ACCOUNT_ID, accountID);
        intent.putExtra(PARAM_CALL_ID, callID);
        intent.putExtra(PARAM_DISPLAY_NAME, displayName);
        intent.putExtra(PARAM_REMOTE_URI, remoteUri);
        intent.putExtra(PARAM_IS_VIDEO, isVideo);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        sendExplicitBroadcast(intent);
    }*/

    /**
     * Emit a registration state broadcast intent.
     *
     * @param accountID             account IdUri
     * @param registrationStateCode SIP registration status code
     */
    public void registrationState(String accountID, int registrationStateCode) {
        final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastAction.REGISTRATION));
        intent.putExtra(PARAM_ACCOUNT_ID, accountID);
        intent.putExtra(PARAM_REGISTRATION_CODE, registrationStateCode);

        mContext.sendBroadcast(intent);
    }

    /**
     * Emit a call state broadcast intent.
     *
     * @param accountID        call's account IdUri
     * @param callID           call ID number
     * @param callStateCode    SIP call state code
     * @param callStateStatus  SIP call state status
     * @param connectTimestamp call start timestamp
     */
    public synchronized void callState(String accountID, int callID, int callStateCode, int callStateStatus, long connectTimestamp) {
        /*final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastAction.CALL_STATE));
        intent.putExtra(PARAM_ACCOUNT_ID, accountID);
        intent.putExtra(PARAM_CALL_ID, callID);
        intent.putExtra(PARAM_CALL_STATE, callStateCode);
        intent.putExtra(PARAM_CALL_STATUS, callStateStatus);
        intent.putExtra(PARAM_CONNECT_TIMESTAMP, connectTimestamp);

        sendExplicitBroadcast(intent);*/
    }

    /**
     * This method is used for sending different calling events to update calling screen to client.
     * See {@link CallEvent}
     *
     * @param event {@link CallEvent}
     */
    public synchronized void callState(CallEvent event) {
        final Intent intent = new Intent();
        intent.putExtra(PARAM_CALL_EVENT, (Parcelable) event);
        intent.setAction(getAction(BroadcastAction.CALL_EVENT));
        mContext.sendBroadcast(intent);
    }

    /**
     * Emit a call state broadcast intent.
     *
     * @param accountID call's account IdUri
     * @param callID    call ID number
     * @param state     MediaState state updated
     * @param value     call media state update value
     */
    public synchronized void callMediaState(String accountID, int callID, MediaState state, boolean value) {
        final Intent intent = new Intent()
                .setAction(getAction(BroadcastAction.CALL_MEDIA_STATE))
                .putExtra(PARAM_ACCOUNT_ID, accountID)
                .putExtra(PARAM_CALL_ID, callID)
                .putExtra(PARAM_MEDIA_STATE_KEY, state)
                .putExtra(PARAM_MEDIA_STATE_VALUE, value);
        mContext.sendBroadcast(intent);
    }

    public void outgoingCall(String accountID, int callID, String number, boolean isVideo, boolean isVideoConference, boolean isTransfer) {
        final Intent intent = new Intent()
                .setAction(getAction(BroadcastAction.OUTGOING_CALL))
                .putExtra(PARAM_ACCOUNT_ID, accountID)
                .putExtra(PARAM_CALL_ID, callID)
                .putExtra(PARAM_NUMBER, number)
                .putExtra(PARAM_IS_VIDEO, isVideo)
                .putExtra(PARAM_IS_VIDEO_CONF, isVideoConference)
                .putExtra(PARAM_IS_TRANSFER, isTransfer);
        sendExplicitBroadcast(intent);
    }

    public void stackStatus(boolean started) {
        final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastAction.STACK_STATUS));
        intent.putExtra(PARAM_STACK_STARTED, started);

        mContext.sendBroadcast(intent);
    }

    public void codecPriorities(ArrayList<CodecPriority> codecPriorities) {
        final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastAction.CODEC_PRIORITIES));
        intent.putParcelableArrayListExtra(PARAM_CODEC_PRIORITIES_LIST, codecPriorities);

        mContext.sendBroadcast(intent);
    }

    public void codecPrioritiesSetStatus(boolean success) {
        final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastAction.CODEC_PRIORITIES_SET_STATUS));
        intent.putExtra(PARAM_SUCCESS, success);

        mContext.sendBroadcast(intent);
    }

    /*void missedCall(String displayName, String uri) {
        final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastAction.MISSED_CALL));
        intent.putExtra(PARAM_DISPLAY_NAME, displayName);
        intent.putExtra(PARAM_REMOTE_URI, uri);

        sendExplicitBroadcast(intent);
    }*/

    void videoSize(int width, int height) {
        final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastAction.VIDEO_SIZE));
        intent.putExtra(PARAM_INCOMING_VIDEO_WIDTH, width);
        intent.putExtra(PARAM_INCOMING_VIDEO_HEIGHT, height);

        sendExplicitBroadcast(intent);
    }

    void callStats(int callID, int duration, String audioCodec, int callStateStatus, RtpStreamStats rx, RtpStreamStats tx) {
        final Intent intent = new Intent()
                .setAction(getAction(BroadcastAction.CALL_STATS))
                .putExtra(PARAM_CALL_ID, callID)
                .putExtra(PARAM_CALL_STATS_DURATION, duration)
                .putExtra(PARAM_CALL_STATS_AUDIO_CODEC, audioCodec)
                .putExtra(PARAM_CALL_STATS_CALL_STATUS, callStateStatus)
                .putExtra(PARAM_CALL_STATS_RX_STREAM, rx)
                .putExtra(PARAM_CALL_STATS_TX_STREAM, tx);
        mContext.sendBroadcast(intent);
    }

    void callReconnectionState(CallReconnectionState state) {
        final Intent intent = new Intent();
        intent.setAction(getAction(BroadcastAction.CALL_RECONNECTION_STATE));
        intent.putExtra(PARAM_CALL_RECONNECTION_STATE, state);
        mContext.sendBroadcast(intent);
    }

    void silentCallStatus(boolean status, String number) {
        final Intent intent = new Intent();
        intent.setAction(getAction(BroadcastAction.SILENT_CALL_STATUS));
        intent.putExtra(PARAM_SILENT_CALL_STATUS, status);
        intent.putExtra(PARAM_NUMBER, number);
        sendExplicitBroadcast(intent);
    }

    void notifyTlsVerifyStatusFailed() {
        final Intent intent = new Intent();
        intent.setAction(getAction(BroadcastAction.NOTIFY_TLS_VERIFY_STATUS_FAILED));
        sendExplicitBroadcast(intent);
    }

    void onInitialize(InitializeStatus initializeStatus) {
        final Intent intent = new Intent();
        intent.setAction(getAction(BroadcastAction.INITIALIZE));
        intent.putExtra(PARAM_INITIALIZE_STATUS, initializeStatus);
        sendExplicitBroadcast(intent);
    }

    public void removeAccount(String accountIDtoRemove) {
        final Intent intent = new Intent();
        intent.setAction(getAction(BroadcastAction.CALLBACK_REMOVE_ACCOUNT));
        intent.putExtra(PARAM_ACCOUNT_ID, accountIDtoRemove);
        sendExplicitBroadcast(intent);
    }

    /**
     * This method is used for sending different type of mediaEvents to client.
     *
     * @param mediaEventType Type of mediaEvent  {@link org.pjsip.pjsua2.pjmedia_event_type}
     */
    public void callMediaEvent(int mediaEventType) {
        Logger.debug(TAG, "sendCallMediaEvent : " + mediaEventType);
        final Intent intent = new Intent();
        intent.putExtra(PARAM_CALL_MEDIA_EVENT_TYPE, mediaEventType);
        intent.setAction(getAction(BroadcastAction.CALL_MEDIA_EVENT));
        mContext.sendBroadcast(intent);
    }

    /**
     * This method is used for sending different type of mediaEvents to client.
     *
     * @param mediaEventType Type of mediaEvent  {@link org.pjsip.pjsua2.pjmedia_event_type}
     */
    public void callMediaEvent(CallMediaEvent mediaEventType) {
        Logger.debug(TAG, "sendCallMediaEvent : " + mediaEventType);
        final Intent intent = new Intent();
        intent.putExtra(PARAM_CALL_MEDIA_EVENT_TYPE, (Parcelable) mediaEventType);
        intent.setAction(getAction(BroadcastAction.CALL_MEDIA_EVENT));
        mContext.sendBroadcast(intent);
    }

    public void errorCallback(String message) {
        final Intent intent = new Intent();
        intent.setAction(getAction(BroadcastAction.CALLBACK_GENERIC_ERROR));
        intent.putExtra(PARAM_ERROR_MESSAGE, message);
        sendExplicitBroadcast(intent);
    }

    /*public void sendMissedCall(boolean isIncomingCall, String number, String linkedUUid,
                               String callerName, long time, long seconds, CallType callType) {
        Intent intent = new Intent();
        intent.putExtra(PARAM_IS_INCOMING_CALL, isIncomingCall);
        intent.putExtra(PARAM_PHONE_NUMBER, number);
        intent.putExtra(PARAM_INCOMING_LINKED_UUID, linkedUUid);
        intent.putExtra(PARAM_DISPLAY_NAME, callerName);
        intent.putExtra(PARAM_TIME, time);
        intent.putExtra(PARAM_SECONDS, seconds);
        intent.putExtra(PARAM_CALL_TYPE, callType);
        intent.setAction(getAction(BroadcastAction.MISSED_CALL));
        mContext.sendBroadcast(intent);
    }*/

    public void missedCall(final MissedCallData missedCallData) {
        Intent intent = new Intent();
        intent.putExtra(PARAM_MISSED_CALL_DATA, missedCallData);
        intent.setAction(getAction(BroadcastAction.MISSED_CALL));
        mContext.sendBroadcast(intent);
    }

    public Intent getExplicitIntent(Intent intent) {
        PackageManager pm = mContext.getPackageManager();
        List<ResolveInfo> matches = pm.queryBroadcastReceivers(intent, 0);

        for (ResolveInfo resolveInfo : matches) {
            ComponentName cn =
                    new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName,
                            resolveInfo.activityInfo.name);

            intent.setComponent(cn);
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        }

        return intent;
    }

    private void sendExplicitBroadcast(Intent intent) {
        PackageManager pm = mContext.getPackageManager();
        List<ResolveInfo> matches = pm.queryBroadcastReceivers(intent, 0);

        for (ResolveInfo resolveInfo : matches) {
            ComponentName cn =
                    new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName,
                            resolveInfo.activityInfo.name);

            intent.setComponent(cn);
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        }

        mContext.sendBroadcast(intent);
    }

    public void holdCall() {
        Intent intent = new Intent();
        intent.setAction(getAction(BroadcastAction.HOLD_CALL));
        mContext.sendBroadcast(intent);
    }

    public void resumeCall() {
        Intent intent = new Intent();
        intent.setAction(getAction(BroadcastAction.RESUME_CALL));
        mContext.sendBroadcast(intent);
    }
}
