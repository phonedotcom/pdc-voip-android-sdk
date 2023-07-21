package com.phone.sip;

import static com.phone.sip.ObfuscationHelper.getValue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;

import com.phone.sip.constants.CallEvent;
import com.phone.sip.constants.CallMediaEvent;
import com.phone.sip.constants.InitializeStatus;
import com.phone.sip.constants.SipServiceConstants;
import com.phone.sip.model.IncomingCallData;
import com.phone.sip.model.MissedCallData;

import java.util.ArrayList;

/**
 * Reference implementation to receive events emitted by the sip service.
 *
 * @author gotev (Aleksandar Gotev)
 */
public class BroadcastEventReceiver extends BroadcastReceiver implements SipServiceConstants {

    private static final String LOG_TAG = BroadcastEventReceiver.class.getSimpleName();

    private Context receiverContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;

        //save context internally for convenience in subclasses, which can get it with
        //getReceiverContext method
        receiverContext = context;

        String action = intent.getAction();

        if (BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.INITIALIZE).equals(action)) {
            onInitialize(intent.getParcelableExtra(PARAM_INITIALIZE_STATUS));
        } else if (BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.REGISTRATION).equals(action)) {
            int stateCode = intent.getIntExtra(PARAM_REGISTRATION_CODE, -1);
            onRegistration(intent.getStringExtra(PARAM_ACCOUNT_ID), stateCode);

        } else if (BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.INCOMING_CALL).equals(action)) {
            onIncomingCall(
                    intent.getParcelableExtra(PARAM_INCOMING_CALL_DATA),
                    intent.getBooleanExtra(PARAM_ANY_ACTIVE_CALL, false)
            );

        } else if (BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_EVENT).equals(action)) {
            onCallEvent(intent.getParcelableExtra(PARAM_CALL_EVENT));

        } else if (BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_MEDIA_STATE).equals(action)) {
            onCallMediaState((MediaState) intent.getSerializableExtra(PARAM_MEDIA_STATE_KEY),
                    intent.getBooleanExtra(PARAM_MEDIA_STATE_VALUE, false));

        } else if (BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.OUTGOING_CALL).equals(action)) {
            onOutgoingCall(intent.getStringExtra(PARAM_ACCOUNT_ID),
                    intent.getIntExtra(PARAM_CALL_ID, -1),
                    intent.getStringExtra(PARAM_NUMBER),
                    intent.getBooleanExtra(PARAM_IS_VIDEO, false),
                    intent.getBooleanExtra(PARAM_IS_VIDEO_CONF, false),
                    intent.getBooleanExtra(PARAM_IS_TRANSFER, false));

        } else if (BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.STACK_STATUS).equals(action)) {
            onStackStatus(intent.getBooleanExtra(PARAM_STACK_STARTED, false));

        } else if (BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CODEC_PRIORITIES).equals(action)) {
            ArrayList<CodecPriority> codecList = intent.getParcelableArrayListExtra(PARAM_CODEC_PRIORITIES_LIST);
            onReceivedCodecPriorities(codecList);

        } else if (BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CODEC_PRIORITIES_SET_STATUS).equals(action)) {
            onCodecPrioritiesSetStatus(intent.getBooleanExtra(PARAM_SUCCESS, false));

        } else if (BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.MISSED_CALL).equals(action)) {
            onMissedCall(intent.getParcelableExtra(PARAM_MISSED_CALL_DATA));

        } else if (BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.VIDEO_SIZE).equals(action)) {
            onVideoSize(intent.getIntExtra(PARAM_INCOMING_VIDEO_WIDTH, H264_DEF_WIDTH),
                    intent.getIntExtra(PARAM_INCOMING_VIDEO_HEIGHT, H264_DEF_HEIGHT));

        } else if (BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_STATS).equals(action)) {
            int callStatus = intent.getIntExtra(PARAM_CALL_STATUS, -1);
            onCallStats(
                    intent.getIntExtra(PARAM_CALL_ID, -1),
                    intent.getIntExtra(PARAM_CALL_STATS_DURATION, 0),
                    intent.getStringExtra(PARAM_CALL_STATS_AUDIO_CODEC), callStatus,
                    intent.getParcelableExtra(PARAM_CALL_STATS_RX_STREAM),
                    intent.getParcelableExtra(PARAM_CALL_STATS_TX_STREAM));

        } else if (BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_RECONNECTION_STATE).equals(action)) {
            onCallReconnectionState((CallReconnectionState) intent.getSerializableExtra(PARAM_CALL_RECONNECTION_STATE));

        } else if (BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.SILENT_CALL_STATUS).equals(action)) {
            onSilentCallStatus(
                    intent.getBooleanExtra(PARAM_SILENT_CALL_STATUS, false),
                    intent.getStringExtra(PARAM_NUMBER)
            );

        } else if (BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.NOTIFY_TLS_VERIFY_STATUS_FAILED).equals(action)) {
            onTlsVerifyStatusFailed();

        } else if (BroadcastEventEmitter.getAction(BroadcastEventEmitter.BroadcastAction.CALL_MEDIA_EVENT).equals(action)) {
            onCallMediaEvent(intent.getParcelableExtra(PARAM_CALL_MEDIA_EVENT_TYPE));
        }
    }

    protected Context getReceiverContext() {
        return receiverContext;
    }

    /**
     * Register this broadcast receiver.
     * It's recommended to register the receiver in Activity's onResume method.
     *
     * @param context context in which to register this receiver
     */
    public void register(final Context context) {

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.REGISTRATION));
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.INCOMING_CALL));
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.CALL_EVENT));
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.CALL_MEDIA_STATE));
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.OUTGOING_CALL));
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.STACK_STATUS));
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.CODEC_PRIORITIES));
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.CODEC_PRIORITIES_SET_STATUS));
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.MISSED_CALL));
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.VIDEO_SIZE));
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.CALL_STATS));
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.CALL_RECONNECTION_STATE));
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.SILENT_CALL_STATUS));
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.NOTIFY_TLS_VERIFY_STATUS_FAILED));
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.INITIALIZE));
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.CALLBACK_REMOVE_ACCOUNT));
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.CALLBACK_GENERIC_ERROR));
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.END_SERVICE_ACTION));
        intentFilter.addAction(BroadcastEventEmitter.getAction(
                BroadcastEventEmitter.BroadcastAction.CALL_MEDIA_EVENT));
        context.registerReceiver(this, intentFilter);
    }

    /**
     * Unregister this broadcast receiver.
     * It's recommended to unregister the receiver in Activity's onPause method.
     *
     * @param context context in which to unregister this receiver
     */
    public void unregister(final Context context) {
        context.unregisterReceiver(this);
    }

    public void onRegistration(String accountID, int registrationStateCode) {
        Logger.debug(LOG_TAG, "onRegistration - accountID: " + getValue(getReceiverContext(), accountID) +
                ", registrationStateCode: " + registrationStateCode);
    }

    /*public void onIncomingCall(String accountID, int callID, String displayName, String remoteUri, boolean isVideo) {
        Logger.debug(LOG_TAG, "onIncomingCall - accountID: " + getValue(getReceiverContext(), accountID) +
                ", callID: " + callID +
                ", displayName: " + displayName +
                ", remoteUri: " + remoteUri);
    }*/

    /*public void onIncomingCall(String displayName, String linkedUUID, boolean activeCall) {
        Logger.debug(LOG_TAG, "onIncomingCall - displayName: " + getValue(getReceiverContext(), displayName) +
                ", linkedUUID: " + linkedUUID +
                ", activeCall: " + activeCall);
    }*/

    public void onIncomingCall(final IncomingCallData incomingCallData, boolean isAnyActiveCall) {
        Logger.debug(LOG_TAG, "onIncomingCall - " +
                "IncomingCallData: " + incomingCallData.toString() +
                ", isAnyActiveCall: " + isAnyActiveCall);
    }

    public void onCallEvent(String accountID, int callID, int callStateCode, int callStatusCode, long connectTimestamp) {
        Logger.debug(LOG_TAG, "onCallState - accountID: " + getValue(getReceiverContext(), accountID) +
                ", callID: " + callID +
                ", callStateCode: " + callStateCode +
                ", callStatusCode: " + callStatusCode +
                ", connectTimestamp: " + connectTimestamp);
    }

    public void onCallEvent(final CallEvent event) {
        Logger.debug(LOG_TAG, "onCallEvent - " + event.toString());
    }

    public void onCallMediaState(String accountID, int callID, MediaState stateType, boolean stateValue) {
        Logger.debug(LOG_TAG, "onCallMediaState - accountID: " + getValue(getReceiverContext(), accountID) +
                ", callID: " + callID +
                ", mediaStateType: " + stateType.name() +
                ", mediaStateValue: " + stateValue);
    }

    public void onCallMediaState(MediaState stateType, boolean stateValue) {
        Logger.debug(LOG_TAG, "onCallMediaState - mediaStateType: " + stateType.name() +
                ", mediaStateValue: " + stateValue);
    }

    public void onCallMediaEvent(CallMediaEvent mediaEvent) {
        Logger.debug(LOG_TAG, "onCallMediaEvent - mediaEvent: " + mediaEvent);
    }

    public void onOutgoingCall(String accountID, int callID, String number, boolean isVideo, boolean isVideoConference, boolean isTransfer) {
        Logger.debug(LOG_TAG, "onOutgoingCall - accountID: " + getValue(getReceiverContext(), accountID) +
                ", callID: " + callID +
                ", number: " + getValue(getReceiverContext(), number));
    }

    public void onStackStatus(boolean started) {
        Logger.debug(LOG_TAG, "SIP service stack " + (started ? "started" : "stopped"));
    }

    public void onReceivedCodecPriorities(ArrayList<CodecPriority> codecPriorities) {
        Logger.debug(LOG_TAG, "Received codec priorities");
        for (CodecPriority codec : codecPriorities) {
            Logger.debug(LOG_TAG, codec.toString());
        }
    }

    public void onCodecPrioritiesSetStatus(boolean success) {
        Logger.debug(LOG_TAG, "Codec priorities " + (success ? "successfully set" : "set error"));
    }

    /*public void onMissedCall(String displayName, String linkedUUID) {
        Logger.debug(LOG_TAG, "Missed call from " + getValue(getReceiverContext(), displayName)
                + " with LinkedUUID " + getValue(getReceiverContext(), linkedUUID));
    }*/

    public void onMissedCall(@NonNull final MissedCallData missedCallData) {
        Logger.debug(LOG_TAG, "Missed call from " + getValue(getReceiverContext(), missedCallData.getCallerName())
                + " with LinkedUUID " + getValue(getReceiverContext(), missedCallData.getLinkedUUID()));
    }

    protected void onVideoSize(int width, int height) {
        Logger.debug(LOG_TAG, "Video resolution " + width + "x" + height);
    }

    protected void onCallStats(int callID, int duration, String audioCodec, int callStatusCode, RtpStreamStats rx, RtpStreamStats tx) {
        Logger.debug(LOG_TAG, "Call Stats sent " + duration + " " + audioCodec);
    }

    protected void onCallReconnectionState(CallReconnectionState state) {
        Logger.debug(LOG_TAG, "Call reconnection state " + state.name());
    }

    protected void onSilentCallStatus(boolean success, String number) {
        Logger.debug(LOG_TAG, "Success: " + success + " for silent call: " + number);
    }

    protected void onTlsVerifyStatusFailed() {
        Logger.debug(LOG_TAG, "TlsVerifyStatusFailed");
    }

    public void onInitialize(InitializeStatus initializeStatus) {
        if (initializeStatus instanceof InitializeStatus.Success) {
            Logger.debug(LOG_TAG, "onInitialize - SUCCESS: " + ((InitializeStatus.Success) initializeStatus).getUsername());
        } else if (initializeStatus instanceof InitializeStatus.Failure) {
            Logger.debug(LOG_TAG, "onInitialize - FAILURE: " + ((InitializeStatus.Failure) initializeStatus).getErrorMessage());
        }
    }
}
