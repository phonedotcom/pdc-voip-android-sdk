package com.phone.sip;

import static com.phone.sip.ObfuscationHelper.getValue;
import static com.phone.sip.constants.PhoneComServiceConstants.SERVICE_FOREGROUND_NOTIFICATION_ID;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Surface;

import com.phone.sip.constants.CallEvent;
import com.phone.sip.constants.InitializeStatus;
import com.phone.sip.constants.SipServiceConstants;
import com.phone.sip.model.IncomingCallData;
import com.phone.sip.model.MissedCallData;

import org.pjsip.pjsua2.AudDevManager;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.CallVidSetStreamParam;
import org.pjsip.pjsua2.CodecInfo;
import org.pjsip.pjsua2.CodecInfoVector2;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.IpChangeParam;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.VidDevManager;
import org.pjsip.pjsua2.pj_qos_type;
import org.pjsip.pjsua2.pjmedia_orient;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_transport_type_e;
import org.pjsip.pjsua2.pjsua_call_vid_strm_op;
import org.pjsip.pjsua2.pjsua_destroy_flag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sip Service.
 *
 * @author gotev (Aleksandar Gotev)
 */
public class SipService extends BackgroundService implements SipServiceConstants {

    private static final String TAG = SipService.class.getSimpleName();

    private List<SipAccountData> mConfiguredAccounts = new ArrayList<>();
    private SipAccountData mConfiguredGuestAccount;
    private static final ConcurrentHashMap<String, SipAccount> mActiveSipAccounts = new ConcurrentHashMap<>();
    private BroadcastEventEmitter mBroadcastEmitter;
    private SipEndpoint mEndpoint;

    public SharedPreferencesHelper getSharedPreferencesHelper() {
        return mSharedPreferencesHelper;
    }

    private SharedPreferencesHelper mSharedPreferencesHelper;
    private volatile boolean mStarted;
    private int callStatus;

    /***   Service Lifecycle Callbacks    ***/

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        enqueueJob(() -> {
            Logger.debug(TAG, "Creating SipService with priority: " + Thread.currentThread().getPriority());

            //loadNativeLibraries();
            mSharedPreferencesHelper = SharedPreferencesHelper.getInstance(SipService.this);
            mBroadcastEmitter = new BroadcastEventEmitter(SipService.this);
            loadConfiguredAccounts();
            addAllConfiguredAccounts();

            Logger.debug(TAG, "SipService created!");
        });
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        enqueueJob(() -> {
            if (intent == null) return;

            String action = intent.getAction();

            if (action == null) return;

            switch (action) {
                case ACTION_SET_ACCOUNT:
                    handleSetAccount(intent);
                    break;
                case ACTION_REMOVE_ACCOUNT:
                    handleRemoveAccount(intent);
                    break;
                case ACTION_RESTART_SIP_STACK:
                    handleRestartSipStack();
                    break;
                case ACTION_MAKE_CALL:
                    handleMakeCall(intent);
                    break;
                case ACTION_HANG_UP_CALL:
                    handleHangUpCall(intent);
                    break;
                case ACTION_HANG_UP_CALLS:
                    handleHangUpActiveCalls(intent);
                    break;
                case ACTION_HOLD_CALLS:
                    handleHoldActiveCalls(intent);
                    break;
                case ACTION_GET_CALL_STATUS:
                    handleGetCallStatus(intent);
                    break;
                case ACTION_SEND_DTMF:
                    handleSendDTMF(intent);
                    break;
                case ACTION_ACCEPT_INCOMING_CALL:
                    handleMakeCallForIncomingCall(intent);
                    //handleAcceptIncomingCall(intent);
                    break;
                case ACTION_DECLINE_INCOMING_CALL:
                    handleDeclineIncomingCall(intent);
                    break;
                case ACTION_SET_HOLD:
                    handleSetCallHold(intent);
                    break;
                case ACTION_TOGGLE_HOLD:
                    handleToggleCallHold(intent);
                    break;
                case ACTION_SET_MUTE:
                    handleSetCallMute(intent);
                    break;
                case ACTION_TOGGLE_MUTE:
                    handleToggleCallMute(intent);
                    break;
                case ACTION_TRANSFER_CALL:
                    handleTransferCall(intent);
                    break;
                case ACTION_ATTENDED_TRANSFER_CALL:
                    handleAttendedTransferCall(intent);
                    break;
                case ACTION_GET_CODEC_PRIORITIES:
                    handleGetCodecPriorities();
                    break;
                case ACTION_SET_CODEC_PRIORITIES:
                    handleSetCodecPriorities(intent);
                    break;
                case ACTION_GET_REGISTRATION_STATUS:
                    handleGetRegistrationStatus(intent);
                    break;
                case ACTION_REFRESH_REGISTRATION:
                    handleRefreshRegistration(intent);
                    break;
                case ACTION_SET_DND:
                    handleSetDND(intent);
                    break;
                case ACTION_SET_INCOMING_VIDEO:
                    handleSetIncomingVideoFeed(intent);
                    break;
                case ACTION_SET_SELF_VIDEO_ORIENTATION:
                    handleSetSelfVideoOrientation(intent);
                    break;
                case ACTION_SET_VIDEO_MUTE:
                    handleSetVideoMute(intent);
                    break;
                case ACTION_START_VIDEO_PREVIEW:
                    handleStartVideoPreview(intent);
                    break;
                case ACTION_STOP_VIDEO_PREVIEW:
                    handleStopVideoPreview(intent);
                    break;
                case ACTION_SWITCH_VIDEO_CAPTURE_DEVICE:
                    handleSwitchVideoCaptureDevice(intent);
                    break;
                case ACTION_MAKE_DIRECT_CALL:
                    handleMakeDirectCall(intent);
                    break;
                case ACTION_RECONNECT_CALL:
                    handleReconnectCall();
                    break;
                case ACTION_MAKE_SILENT_CALL:
                    handleMakeSilentCall(intent);
                    break;
                case ACTION_INCOMING_CALL_NOTIFICATION:
                    //TODO: Handle Incoming Call Notification
                    handleIncomingCallNotification(intent);
                    break;
                case ACTION_INCOMING_CALL_DISCONNECTED:
                    handleIncomingCallDisconnected(intent);
                    break;
                case ACTION_REJECT_CALL_USER_BUSY:
                    handleRejectIncomingCallUserBusy(intent);
                    break;
                case ACTION_UNREGISTER_PUSH_LOGOUT:
                    handleUnregisterPushAndLogout(intent);
                    break;
                default:
                    break;
            }

            if (mConfiguredAccounts.isEmpty() && mConfiguredGuestAccount == null) {
                Logger.debug(TAG, "No more configured accounts. Shutting down service");
                stopSelf();
            }
        });

        return START_NOT_STICKY;
    }

    private void handleUnregisterPushAndLogout(Intent intent) {

        Logger.debug(TAG, "handleUnregisterPushAndLogout -> ------Logout Initiated for "
                + getApplicationInfo().loadLabel(getPackageManager()).toString());
        startForeground(NotificationCreator.createForegroundServiceNotification(this, this.getApplicationInfo().loadLabel(getPackageManager()).toString()));

        final SipAccount sipAccount = getActiveSipAccount(this);
        if (sipAccount != null) {
            try {
                Logger.debug("IdURI => ", sipAccount.getData().getIdUri(this));
                Logger.debug("IdURI => ", sipAccount.getData().getUsername());
                sipAccount.modify(sipAccount.getData().getAccountConfigForUnregister(getApplicationContext()));
                new Handler(Looper.myLooper()).postDelayed(() -> {
                    try {
                        sipAccount.setRegistration(false);
                        SharedPreferencesHelper.getInstance(SipService.this).clearAllSharedPreferences();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }, 2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        stopForegroundService(sipAccount);
        Logger.debug(TAG, "handleUnregisterPushAndLogout -> ------Logout Completed-----");
    }

    private void handleIncomingCallDisconnected(Intent intent) {
        Logger.debug(TAG, "handleIncomingCallDisconnected");
        String linkedUUID = intent.getStringExtra(SipServiceConstants.PARAM_INCOMING_LINKED_UUID);
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        SipAccount sipAccount = mActiveSipAccounts.get(accountID);
        if (sipAccount == null) {
            mBroadcastEmitter.errorCallback(SipServiceConstants.ERR_SIP_ACCOUNT_NULL);
            return;
        }

        startForeground(NotificationCreator.createForegroundServiceNotification(this, this.getApplicationInfo().loadLabel(getPackageManager()).toString()));

        ICall activeIncomingCall = sipAccount.getActiveIncomingCall();
        if (activeIncomingCall != null && activeIncomingCall.getLinkedUUID().equalsIgnoreCase(linkedUUID) &&
                activeIncomingCall.getState().equals(CallState.INCOMING_CALL)) {

            String number = intent.getStringExtra(SipServiceConstants.PARAM_INCOMING_FROM);
            IncomingCall incomingCallObject = SipUtility.createIncomingCallObject(intent);
            incomingCallObject.setCallType(CallType.MISSED);
            handleMissedCall(incomingCallObject, number);

            mBroadcastEmitter.callState(CallEvent.DISCONNECTED);
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            audioManager.setSpeakerphoneOn(false);

            MediaPlayerController.getInstance(this).resumeMusicPlayer();
        }

        // disconnect call if active
        stopForegroundService(sipAccount);
    }

    public synchronized void stopForegroundService(SipAccount sipAccount) {
        if (sipAccount == null) {
            stopForeground(true);
            return;
        }
        Logger.debug(TAG, "stopForegroundService");
        stopForeground(true);
        sipAccount.setActiveIncomingCall(null);
        Logger.debug(TAG, "stopForegroundService -> After Set ActiveIncomingCall to NULL");
    }

    private void handleMissedCall(ICall call, String number) {
        mBroadcastEmitter.missedCall(new MissedCallData(call.getCallerName(), call.getLinkedUUID(), 0));
    }

    private void handleIncomingCallNotification(Intent intent) {
        //Stop Music (if any)
        MediaPlayerController.getInstance(this).stopMusicPlayer();
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);

        final ICall iCall = SipUtility.createIncomingCallObject(intent);
        SipAccount sipAccount = mActiveSipAccounts.get(accountID);
        if (sipAccount == null) {
            mBroadcastEmitter.errorCallback(SipServiceConstants.ERR_SIP_ACCOUNT_NULL);
            return;
        }
        sipAccount.setActiveIncomingCall(iCall);
        //TODO : If required, put notification here

        notifyIncomingCallNotification(intent, iCall);
    }

    private void notifyIncomingCallNotification(Intent intent, ICall iCall) {
        final IncomingCall incomingCall = (IncomingCall) iCall;
        //incomingCall.setVideoCall(isVideo);
        startForeground(NotificationCreator.createForegroundServiceNotification(this, incomingCall.getCallerName()));

        getActiveSipAccount(this).setActiveIncomingCall(incomingCall);

        final IncomingCallData incomingCallData = new IncomingCallData(
                incomingCall.getNumber(),
                incomingCall.getCallerName(),
                incomingCall.getLinkedUUID(),
                SERVICE_FOREGROUND_NOTIFICATION_ID,
                false
        );

        getBroadcastEmitter().incomingCall(
                incomingCallData, getActiveSipAccount(this).isActiveCallPresent()
        );

    }

    @Override
    public void onDestroy() {
        enqueueJob(() -> {
            Logger.debug(TAG, "Destroying SipService");
            stopStack();
        });
        super.onDestroy();
    }

    @Override
    void checkThread(Thread thread) {
        try {
            if (mEndpoint != null && !mEndpoint.libIsThreadRegistered())
                mEndpoint.libRegisterThread(thread.getName());
        } catch (Exception e) {
            Logger.error(TAG, "CheckThread -> Threading: libRegisterThread failed: " + e.getMessage());
        }
    }

    /***   Sip Calls Management    ***/

    private SipCall getCall(String accountID, int callID) {
        SipAccount account = mActiveSipAccounts.get(accountID);

        if (account == null) return null;
        SipCall sipCall = account.getCall(callID);
        if (sipCall != null) {
            return sipCall;
        } /*else if (account.isActiveCallPresent()) {
            return account.getActiveCall();
        } */ else {
            notifyCallDisconnected(accountID, callID);
            return null;
        }
    }

    private void notifyCallDisconnected() {
        mBroadcastEmitter.callState(CallEvent.DISCONNECTED);
    }

    private void notifyCallDisconnected(String accountID, int callID) {
        mBroadcastEmitter.callState(accountID, callID,
                pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED,
                callStatus, 0);
    }

    private void handleGetCallStatus(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(PARAM_CALL_ID, 0);

        SipCall sipCall = getCall(accountID, callID);
        if (sipCall != null) {
            int callStatusCode = callStatus;
            try {
                callStatusCode = sipCall.getInfo().getLastStatusCode();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            mBroadcastEmitter.callState(accountID, callID, sipCall.getCurrentState(), callStatusCode, sipCall.getConnectTimestamp());
        }
    }

    private void handleSendDTMF(Intent intent) {
        final String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        final String dtmf = intent.getStringExtra(PARAM_DTMF);

        final Set<Integer> activeCallIDs = getActiveSipAccount(accountID).getCallIDs();

        if (activeCallIDs == null || activeCallIDs.isEmpty()) return;

        SipCall sipCall = null;

        for (int callID : activeCallIDs) {
            try {
                sipCall = getCall(accountID, callID);
            } catch (Exception exc) {
                Logger.error(TAG, "Error while hanging up call", exc);
                notifyCallDisconnected();
            }
        }

        if (sipCall != null) {
            try {
                SipUtility.playSound(dtmf + ".wav", this.getApplicationContext());
                sipCall.dialDtmf(dtmf);
                if (dtmf.equals(DTMFCodes.NINE.toString())) {
                    final SipAccount sipAccount = mActiveSipAccounts.get(accountID);
                    if (sipAccount != null)
                        stopForegroundService(sipAccount);
                }
            } catch (Exception exc) {
                Logger.error(TAG, "Error while dialing DTMF: " + dtmf + ". AccountID: "
                        + getValue(getApplicationContext(), accountID));
            }
        }
    }

    private void handleAcceptIncomingCall(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(PARAM_CALL_ID, 0);

        SipCall sipCall = getCall(accountID, callID);
        if (sipCall != null) {
            boolean isVideo = intent.getBooleanExtra(PARAM_IS_VIDEO, false);
            try {
                sipCall.setVideoParams(isVideo, false);
                sipCall.acceptIncomingCall();
            } catch (Exception exc) {
                Logger.error(TAG, "Error while accepting incoming call. AccountID: "
                        + getValue(getApplicationContext(), accountID) + ", CallID: " + callID);
            }
        }
    }

    private void handleSetCallHold(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(PARAM_CALL_ID, 0);

        SipCall sipCall = getCall(accountID, callID);
        if (sipCall != null) {
            boolean hold = intent.getBooleanExtra(PARAM_HOLD, false);
            try {
                sipCall.setHold(hold);
            } catch (Exception exc) {
                Logger.error(TAG, "Error while setting hold. AccountID: "
                        + getValue(getApplicationContext(), accountID) + ", CallID: " + callID);
            }
        }
    }

    private void handleToggleCallHold(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(PARAM_CALL_ID, 0);

        SipCall sipCall = getCall(accountID, callID);
        if (sipCall != null) {
            try {
                boolean isHold = sipCall.toggleHold();
                if(isHold){
                    mBroadcastEmitter.holdCall();
                } else {
                    mBroadcastEmitter.resumeCall();
                }
            } catch (Exception exc) {
                Logger.error(TAG, "Error while toggling hold. AccountID: "
                        + getValue(getApplicationContext(), accountID) + ", CallID: " + callID);
                mBroadcastEmitter.errorCallback(exc.getMessage());
            }
        }
    }

    private void handleSetCallMute(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(PARAM_CALL_ID, 0);

        // TODO for lift master - as there is no multi call support we can fetch the peek call from active calls, in other case we will need call id from application

        SipCall sipCall = getCall(accountID, callID);
        if (sipCall != null) {
            boolean mute = intent.getBooleanExtra(PARAM_MUTE, false);
            try {
                sipCall.setMute(mute);
            } catch (Exception exc) {
                Logger.error(TAG, "Error while setting mute. AccountID: "
                        + getValue(getApplicationContext(), accountID) + ", CallID: " + callID);
                mBroadcastEmitter.errorCallback("Error while setting mute. AccountID: "
                        + getValue(getApplicationContext(), accountID) + ", CallID: " + callID);
            }
        } else {
            mBroadcastEmitter.errorCallback(SipServiceConstants.ERR_SIP_CALL_NULL);
        }
    }

    private void handleToggleCallMute(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(PARAM_CALL_ID, 0);

        SipCall sipCall = getCall(accountID, callID);
        if (sipCall != null) {
            try {
                sipCall.toggleMute();
            } catch (Exception exc) {
                Logger.error(TAG, "Error while toggling mute. AccountID: "
                        + getValue(getApplicationContext(), accountID) + ", CallID: " + callID);
                mBroadcastEmitter.errorCallback("Error while toggling mute. AccountID: "
                        + getValue(getApplicationContext(), accountID) + ", CallID: " + callID);
            }
        } else {
            mBroadcastEmitter.errorCallback(SipServiceConstants.ERR_SIP_CALL_NULL);
        }
    }

    private void handleDeclineIncomingCall(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        SipAccount sipAccount = mActiveSipAccounts.get(accountID);
        if (sipAccount == null) {
            mBroadcastEmitter.errorCallback(SipServiceConstants.ERR_SIP_ACCOUNT_NULL);
            return;
        }

        try {
            final IncomingCall incomingCall = (IncomingCall) sipAccount.getActiveIncomingCall();

            final String incomingFrom = incomingCall.getNumber();
            final String incomingSlot = incomingCall.getSlot();
            final String incomingServer = incomingCall.getServer();
            final String incomingLinkedUuid = incomingCall.getLinkedUUID();
            boolean isVideo = intent.getBooleanExtra(PARAM_IS_VIDEO, false);

            sipAccount.declineIncomingCall(incomingFrom,
                    incomingSlot,
                    incomingServer,
                    incomingLinkedUuid,
                    isVideo);
            //stopCallForegroundService(sipAccount);
        } catch (Exception exc) {
            Logger.error(TAG, "Error while declining incoming call. AccountID: "
                    + getValue(getApplicationContext(), accountID));
            mBroadcastEmitter.errorCallback("Error while declining incoming call. AccountID: "
                    + getValue(getApplicationContext(), accountID));
        }
    }

    private void handleRejectIncomingCallUserBusy(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        SipAccount sipAccount = mActiveSipAccounts.get(accountID);
        if (sipAccount == null) {
            mBroadcastEmitter.errorCallback(SipServiceConstants.ERR_SIP_ACCOUNT_NULL);
            return;
        }

        try {
            final IncomingCall incomingCall = (IncomingCall) sipAccount.getActiveIncomingCall();

            final String incomingFrom = incomingCall.getNumber();
            final String incomingSlot = incomingCall.getSlot();
            final String incomingServer = incomingCall.getServer();
            final String incomingLinkedUuid = incomingCall.getLinkedUUID();
            boolean isVideo = intent.getBooleanExtra(PARAM_IS_VIDEO, false);
            final String errorCode = intent.getStringExtra(PARAM_ERROR_CODE_WHILE_REJECTING_INCOMING_CALL);

            sipAccount.rejectIncomingCallUserBusy(incomingFrom,
                    incomingSlot,
                    incomingServer,
                    incomingLinkedUuid,
                    isVideo, errorCode);
            stopForegroundService(sipAccount);
        } catch (Exception exc) {
            Logger.error(TAG, "Error while declining incoming call. AccountID: "
                    + getValue(getApplicationContext(), accountID));
            mBroadcastEmitter.errorCallback("Error while declining incoming call. AccountID: "
                    + getValue(getApplicationContext(), accountID));
        }
    }

    private void handleHangUpCall(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);

        SipAccount account = mActiveSipAccounts.get(accountID);
        if (account == null) return;

        int callID = intent.getIntExtra(PARAM_CALL_ID, 0);

        try {
            // stopCallForegroundService() has to call before hangupCall() here, because from the hangup we receives the broadcast and remove the call from that event hence, putting the stopCallForegroundService() after will falsify the condition.
            stopForegroundService(account);
            hangupCall(accountID, callID);
        } catch (Exception exc) {
            Logger.error(TAG, "Error while hanging up call", exc);
            notifyCallDisconnected(accountID, callID);
        }
    }

    private void handleHangUpActiveCalls(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);

        SipAccount account = mActiveSipAccounts.get(accountID);
        if (account == null) return;

        Set<Integer> activeCallIDs = account.getCallIDs();

        if (activeCallIDs == null || activeCallIDs.isEmpty()) return;

        startForeground(NotificationCreator.createForegroundServiceNotification(this, SipServiceConstants.PARAM_APP_NAME));

        for (int callID : activeCallIDs) {
            try {
                // stopCallForegroundService() has to call before hangupCall() here, because from the hangup we receives the broadcast and remove the call from that event hence, putting the stopCallForegroundService() after will falsify the condition.
                //stopCallForegroundService(account);
                hangupCall(accountID, callID);
            } catch (Exception exc) {
                Logger.error(TAG, "Error while hanging up call", exc);
                notifyCallDisconnected(accountID, callID);
            }
        }
    }

    private void hangupCall(String accountID, int callID) throws Exception {
        SipCall sipCall = getCall(accountID, callID);
        if (sipCall != null) {
            sipCall.hangUp();
        }
    }

    private void handleHoldActiveCalls(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);

        SipAccount account = mActiveSipAccounts.get(accountID);
        if (account == null) return;

        Set<Integer> activeCallIDs = account.getCallIDs();

        if (activeCallIDs == null || activeCallIDs.isEmpty()) return;

        for (int callID : activeCallIDs) {
            try {
                SipCall sipCall = getCall(accountID, callID);
                if (sipCall != null) {
                    sipCall.setHold(true);
                }
            } catch (Exception exc) {
                Logger.error(TAG, "Error while holding call", exc);
            }
        }
    }

    private void handleTransferCall(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(PARAM_CALL_ID, 0);
        String number = intent.getStringExtra(PARAM_NUMBER);

        try {
            SipCall sipCall = getCall(accountID, callID);
            if (sipCall != null) {
                sipCall.transferTo(number);
            }
        } catch (Exception exc) {
            Logger.error(TAG, "Error while transferring call to " + getValue(getApplicationContext(), number), exc);
            notifyCallDisconnected(accountID, callID);
        }
    }

    private void handleAttendedTransferCall(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        int callIdOrig = intent.getIntExtra(PARAM_CALL_ID, 0);

        try {
            SipCall sipCallOrig = getCall(accountID, callIdOrig);
            if (sipCallOrig != null) {
                int callIdDest = intent.getIntExtra(PARAM_CALL_ID_DEST, 0);
                SipCall sipCallDest = getCall(accountID, callIdDest);
                sipCallOrig.xferReplaces(sipCallDest, new CallOpParam());
            }
        } catch (Exception exc) {
            Logger.error(TAG, "Error while finalizing attended transfer", exc);
            notifyCallDisconnected(accountID, callIdOrig);
        }
    }

    private void handleSetIncomingVideoFeed(Intent intent) {
        //startForeground(SERVICE_FOREGROUND_NOTIFICATION_ID, createForegroundServiceNotification(this, getString(R.string.app_name)));
        Logger.debug(TAG, "handleSetIncomingVideoFeed()");
        final String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);

        final Set<Integer> activeCallIDs = getActiveSipAccount(accountID).getCallIDs();

        if (activeCallIDs == null || activeCallIDs.isEmpty()) {
            startForeground(NotificationCreator.createForegroundServiceNotification(this, SipServiceConstants.PARAM_APP_NAME));
            enqueueDelayedJob(() -> stopForeground(true), SipServiceConstants.DELAY_STOP_SERVICE);
            return;
        }

        SipCall sipCall = null;

        for (int callID : activeCallIDs) {
            try {
                sipCall = getCall(accountID, callID);
            } catch (Exception exc) {
                Logger.error(TAG, "Error while hanging up call", exc);
                notifyCallDisconnected(accountID, callID);
            }
        }

        final Bundle bundle = intent.getExtras();
        if (sipCall != null && bundle != null) {
            Logger.debug(TAG, "handleSetIncomingVideoFeed() -> Surface NOT NULL");
            Surface surface = bundle.getParcelable(PARAM_SURFACE);
            sipCall.setIncomingVideoFeed(surface);
        } else {
            Logger.debug(TAG, "handleSetIncomingVideoFeed() -> Surface NULL");
            startForeground(NotificationCreator.createForegroundServiceNotification(this, SipServiceConstants.PARAM_APP_NAME));
            enqueueDelayedJob(() -> stopForeground(true), SipServiceConstants.DELAY_STOP_SERVICE);
        }

    }

    private void handleSetSelfVideoOrientation(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(PARAM_CALL_ID, 0);

        SipAccount sipAccount = mActiveSipAccounts.get(accountID);
        if (sipAccount != null) {
            SipCall sipCall = getCall(accountID, callID);
            if (sipCall != null) {
                int orientation = intent.getIntExtra(PARAM_ORIENTATION, -1);
                setSelfVideoOrientation(sipCall, orientation);
            }
        }
    }

    void setSelfVideoOrientation(SipCall sipCall, int orientation) {
        try {
            int pjmediaOrientation;

            switch (orientation) {
                case Surface.ROTATION_0:   // Portrait
                    pjmediaOrientation = pjmedia_orient.PJMEDIA_ORIENT_ROTATE_270DEG;
                    break;
                case Surface.ROTATION_90:  // Landscape, home button on the right
                    pjmediaOrientation = pjmedia_orient.PJMEDIA_ORIENT_NATURAL;
                    break;
                case Surface.ROTATION_180:
                    pjmediaOrientation = pjmedia_orient.PJMEDIA_ORIENT_ROTATE_90DEG;
                    break;
                case Surface.ROTATION_270: // Landscape, home button on the left
                    pjmediaOrientation = pjmedia_orient.PJMEDIA_ORIENT_ROTATE_180DEG;
                    break;
                default:
                    pjmediaOrientation = pjmedia_orient.PJMEDIA_ORIENT_UNKNOWN;
            }

            if (pjmediaOrientation != pjmedia_orient.PJMEDIA_ORIENT_UNKNOWN)
                // set orientation to the correct current device
                getVidDevManager().setCaptureOrient(
                        sipCall.isFrontCamera()
                                ? FRONT_CAMERA_CAPTURE_DEVICE
                                : BACK_CAMERA_CAPTURE_DEVICE,
                        pjmediaOrientation, true);

        } catch (Exception iex) {
            Logger.error(TAG, "Error while changing video orientation");
        }
    }

    private void handleSetVideoMute(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(PARAM_CALL_ID, 0);

        SipCall sipCall = getCall(accountID, callID);
        if (sipCall != null) {
            boolean mute = intent.getBooleanExtra(PARAM_VIDEO_MUTE, false);
            try {
                sipCall.setVideoMute(mute);
            } catch (Exception e) {
                mBroadcastEmitter.errorCallback("Error while setting mute. AccountID: "
                        + getValue(getApplicationContext(), accountID) + ", CallID: " + callID);
            }
        } else {
            mBroadcastEmitter.errorCallback(SipServiceConstants.ERR_SIP_CALL_NULL);
        }
    }

    private void handleStartVideoPreview(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(PARAM_CALL_ID, 0);

        SipCall sipCall = getCall(accountID, callID);
        if (sipCall != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Surface surface = intent.getExtras().getParcelable(PARAM_SURFACE);
                sipCall.startPreviewVideoFeed(surface);
            }
        }
    }

    private void handleStopVideoPreview(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(PARAM_CALL_ID, 0);

        SipCall sipCall = getCall(accountID, callID);
        if (sipCall != null) {
            sipCall.stopPreviewVideoFeed();
        }
    }

    // Switch Camera
    private void handleSwitchVideoCaptureDevice(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(PARAM_CALL_ID, 0);

        final SipCall sipCall = getCall(accountID, callID);
        if (sipCall != null) {
            try {
                CallVidSetStreamParam callVidSetStreamParam = new CallVidSetStreamParam();
                callVidSetStreamParam.setCapDev(sipCall.isFrontCamera()
                        ? BACK_CAMERA_CAPTURE_DEVICE
                        : FRONT_CAMERA_CAPTURE_DEVICE);
                sipCall.setFrontCamera(!sipCall.isFrontCamera());
                sipCall.vidSetStream(pjsua_call_vid_strm_op.PJSUA_CALL_VID_STRM_CHANGE_CAP_DEV, callVidSetStreamParam);
            } catch (Exception ex) {
                Logger.error(TAG, "Error while switching capture device", ex);
            }
        }
    }

    private void handleMakeCallForIncomingCall(Intent intent) {
        Logger.debug(TAG, "handleMakeCallForIncomingCall()");

        final String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);

        final IncomingCall incomingCall = (IncomingCall) getActiveSipAccount(accountID).getActiveIncomingCall();

        if (incomingCall == null) {
            mBroadcastEmitter.callState(CallEvent.DISCONNECTED);
            return;
        }

        final String incomingFrom = incomingCall.getNumber();
        final String incomingSlot = incomingCall.getSlot();
        final String incomingServer = incomingCall.getServer();
        final String incomingLinkedUuid = incomingCall.getLinkedUUID();
        final String callerName = incomingCall.getCallerName();

        /*final String incomingFrom = intent.getStringExtra(PARAM_INCOMING_FROM);
        final String incomingSlot = intent.getStringExtra(PARAM_INCOMING_SLOT);
        final String incomingServer = intent.getStringExtra(PARAM_INCOMING_SERVER);
        final String incomingLinkedUuid = intent.getStringExtra(PARAM_INCOMING_LINKED_UUID);*/

        boolean isVideo = intent.getBooleanExtra(PARAM_IS_VIDEO, false);
        boolean isVideoConference = false;

        Logger.debug(TAG, "Making call to " + getValue(getApplicationContext(), incomingFrom));

        try {
//            SipCall call = mActiveSipAccounts.get(accountID).addOutgoingCall(number, isVideo, isVideoConference, isTransfer);
            final SipCall call = getActiveSipAccount(accountID).addOutgoingForIncomingCall(
                    incomingFrom,
                    incomingSlot,
                    incomingServer,
                    incomingLinkedUuid,
                    callerName,
                    isVideo
            );
            //call.setVideoParams(isVideo, isVideoConference);
            mBroadcastEmitter.callState(CallEvent.CONNECTING);
        } catch (Exception exc) {
            Logger.error(TAG, "Error while making outgoing call", exc);
            mBroadcastEmitter.callState(CallEvent.DISCONNECTED);
        }
    }

    private void handleMakeCall(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        String number = intent.getStringExtra(PARAM_NUMBER);
        boolean isVideo = intent.getBooleanExtra(PARAM_IS_VIDEO, false);
        boolean isVideoConference = false;
        boolean isTransfer = false;
        if (isVideo) {
            isVideoConference = intent.getBooleanExtra(PARAM_IS_VIDEO_CONF, false);
            // do not allow attended transfer on video call for now
        } else {
            isTransfer = intent.getBooleanExtra(PARAM_IS_TRANSFER, false);
        }

        Logger.debug(TAG, "Making call to " + getValue(getApplicationContext(), number));

        try {
            SipCall call = mActiveSipAccounts.get(accountID).addOutgoingCall(number, isVideo, isVideoConference, isTransfer);
            call.setVideoParams(isVideo, isVideoConference);
            mBroadcastEmitter.outgoingCall(accountID, call.getId(), number, isVideo, isVideoConference, isTransfer);
        } catch (Exception exc) {
            Logger.error(TAG, "Error while making outgoing call", exc);
            mBroadcastEmitter.outgoingCall(accountID, -1, number, false, false, false);
        }
    }

    private void handleMakeDirectCall(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;
        Uri uri = bundle.getParcelable(PARAM_DIRECT_CALL_URI);
        if (uri == null) return;
        String sipServer = intent.getStringExtra(PARAM_DIRECT_CALL_SIP_SERVER);
        String name = intent.getStringExtra(PARAM_GUEST_NAME);
        boolean isVideo = intent.getBooleanExtra(PARAM_IS_VIDEO, false);
        boolean isVideoConference = false;
        if (isVideo) {
            isVideoConference = intent.getBooleanExtra(PARAM_IS_VIDEO_CONF, false);
        }
        SipAccountTransport transport = SipAccountTransport.getTransportByCode(
                intent.getIntExtra(PARAM_DIRECT_CALL_TRANSPORT, 0)
        );

        Logger.debug(TAG, "Making call to " + getValue(getApplicationContext(), uri.getUserInfo()));
        String accountID = "sip:" + name + "@" + uri.getHost();
        String sipUri = "sip:" + uri.getUserInfo() + "@" + uri.getHost();

        try {
            startStack();
            SipAccountData sipAccountData = new SipAccountData()
                    .setHost(sipServer != null ? sipServer : uri.getHost())
                    .setUsername(name)
                    .setPort((uri.getPort() > 0) ? uri.getPort() : DEFAULT_SIP_PORT)
                    .setTransport(transport)
                    .setRealm(uri.getHost());
            /* display name not yet implemented server side for direct calls */
            /* .setUsername("guest") */
            /* .setGuestDisplayName(name)*/
            SipAccount pjSipAndroidAccount = new SipAccount(this, sipAccountData);
            pjSipAndroidAccount.createGuest();
            mConfiguredGuestAccount = pjSipAndroidAccount.getData();

            // Overwrite the old value if present
            mActiveSipAccounts.put(accountID, pjSipAndroidAccount);

            SipCall call = mActiveSipAccounts.get(accountID).addOutgoingCall(sipUri, isVideo, isVideoConference, false);
            if (call != null) {
                call.setVideoParams(isVideo, isVideoConference);
                mBroadcastEmitter.outgoingCall(accountID, call.getId(), uri.getUserInfo(), isVideo, isVideoConference, false);
            } else {
                Logger.error(TAG, "Error while making a direct call as Guest");
                mBroadcastEmitter.outgoingCall(accountID, -1, uri.getUserInfo(), false, false, false);
            }
        } catch (Exception ex) {
            Logger.error(TAG, "Error while making a direct call as Guest", ex);
            mBroadcastEmitter.outgoingCall(accountID, -1, uri.getUserInfo(), false, false, false);
        }
    }

    private void handleMakeSilentCall(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        String number = intent.getStringExtra(PARAM_NUMBER);

        Logger.debug(TAG, "Making silent call to " + getValue(getApplicationContext(), number));

        try {
            mBroadcastEmitter.silentCallStatus(mActiveSipAccounts.get(accountID).addOutgoingCall(number) != null, number);
        } catch (Exception exc) {
            mBroadcastEmitter.silentCallStatus(false, number);
            Logger.error(TAG, "Error while making silent call", exc);
        }
    }

    private void handleReconnectCall() {
        try {
            getBroadcastEmitter().callReconnectionState(CallReconnectionState.PROGRESS);
            mEndpoint.handleIpChange(new IpChangeParam());
            Logger.info(TAG, "Call reconnection started");
        } catch (Exception exc) {
            Logger.error(TAG, "Error while reconnecting the call", exc);
        }
    }

    public void setLastCallStatus(int callStatus) {
        this.callStatus = callStatus;
    }

    /***   Sip Account Management    ***/

    private void handleRefreshRegistration(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);
        int regExpTimeout = intent.getIntExtra(PARAM_REG_EXP_TIMEOUT, 0);
        String regContactParams = intent.getStringExtra(PARAM_REG_CONTACT_PARAMS);
        boolean refresh = true;
        if (!mActiveSipAccounts.isEmpty() && mActiveSipAccounts.containsKey(accountID)) {
            try {
                SipAccount sipAccount = mActiveSipAccounts.get(accountID);
                if (sipAccount == null) return;

                if (regExpTimeout != 0 && regExpTimeout != sipAccount.getData().getRegExpirationTimeout()) {
                    sipAccount.getData().setRegExpirationTimeout(regExpTimeout);
                    Logger.debug(TAG, String.valueOf(regExpTimeout));
                    refresh = false;
                }
                if (regContactParams != null && !(regContactParams.equals(sipAccount.getData().getContactUriParams()))) {
                    Logger.debug(TAG, getValue(getApplicationContext(), regContactParams));
                    sipAccount.getData().setContactUriParams(regContactParams);
                    refresh = false;
                    mActiveSipAccounts.put(accountID, sipAccount);
                    mConfiguredAccounts.clear();
                    mConfiguredAccounts.add(sipAccount.getData());
                    persistConfiguredAccounts();
                }
                if (refresh) {
                    sipAccount.setRegistration(true);
                } else {
                    sipAccount.modify(sipAccount.getData().getAccountConfig(getApplicationContext()));
                    sipAccount.getData().setRegExpirationTimeout(100);
                }
            } catch (Exception ex) {
                Logger.error(TAG, "Error while refreshing registration");
                ex.printStackTrace();
            }
        } else {
            Logger.debug(TAG, "account " + getValue(getApplicationContext(), accountID) + " not set");
        }
    }

    private void handleRestartSipStack() {
        Logger.debug(TAG, "Restarting SIP stack");
        stopStack();
        addAllConfiguredAccounts();
    }

    private void handleResetAccounts() {
        Logger.debug(TAG, "Removing all the configured accounts");

        Iterator<SipAccountData> iterator = mConfiguredAccounts.iterator();

        while (iterator.hasNext()) {
            SipAccountData data = iterator.next();

            try {
                removeAccount(data.getIdUri(getApplicationContext()));
                iterator.remove();
            } catch (Exception exc) {
                Logger.error(TAG, "Error while removing account " + getValue(getApplicationContext(), data.getIdUri(getApplicationContext())), exc);
            }
        }

        persistConfiguredAccounts();
    }

    private void handleRemoveAccount(Intent intent) {
        String accountIDtoRemove = intent.getStringExtra(PARAM_ACCOUNT_ID);

        Logger.debug(TAG, "Removing " + getValue(getApplicationContext(), accountIDtoRemove));

        Iterator<SipAccountData> iterator = mConfiguredAccounts.iterator();

        while (iterator.hasNext()) {
            SipAccountData data = iterator.next();

            if (data.getIdUri(getApplicationContext()).equals(accountIDtoRemove)) {
                try {
                    removeAccount(accountIDtoRemove);
                    iterator.remove();
                    persistConfiguredAccounts();
                } catch (Exception exc) {
                    Logger.error(TAG, "Error while removing account " + getValue(getApplicationContext(), accountIDtoRemove), exc);
                }
                break;
            }
        }
    }

    private void handleSetAccount(Intent intent) {
        startForeground(NotificationCreator.createForegroundServiceNotification(this, PARAM_APP_NAME));
        SipAccountData data = intent.getParcelableExtra(PARAM_ACCOUNT_DATA);

        int index = mConfiguredAccounts.indexOf(data);
        if (index == -1) {
            handleResetAccounts();
            Logger.debug(TAG, "Adding " + getValue(getApplicationContext(), data.getIdUri(getApplicationContext())));

            try {
                handleSetCodecPriorities(intent);
                addAccount(data);
                mConfiguredAccounts.add(data);
                persistConfiguredAccounts();
                mBroadcastEmitter.onInitialize(new InitializeStatus.Success(data.getUsername()));
            } catch (Exception exc) {
                Logger.error(TAG, "Error while adding " + getValue(getApplicationContext(), data.getIdUri(getApplicationContext())), exc);
                enqueueDelayedJob(() -> stopForeground(false), SipServiceConstants.DELAY_STOP_SERVICE);
                mBroadcastEmitter.onInitialize(new InitializeStatus.Failure("Error while adding " + getValue(getApplicationContext(), data.getIdUri(getApplicationContext()))));
                return;
            }
        } else {
            Logger.debug(TAG, "Reconfiguring " + getValue(getApplicationContext(), data.getIdUri(getApplicationContext())));

            try {
                //removeAccount(data.getIdUri());
                handleSetCodecPriorities(intent);
                addAccount(data);
                mConfiguredAccounts.set(index, data);
                persistConfiguredAccounts();
                mBroadcastEmitter.onInitialize(new InitializeStatus.Success(data.getUsername()));
            } catch (Exception exc) {
                Logger.error(TAG, "Error while reconfiguring " + getValue(getApplicationContext(), data.getIdUri(getApplicationContext())), exc);
                mBroadcastEmitter.onInitialize(new InitializeStatus.Failure("Error while adding " + getValue(getApplicationContext(), data.getIdUri(getApplicationContext()))));
                enqueueDelayedJob(() -> stopForeground(false), SipServiceConstants.DELAY_STOP_SERVICE);
                return;
            }
        }

        enqueueDelayedJob(() -> stopForeground(false), SipServiceConstants.DELAY_STOP_SERVICE);
    }

    private void handleGetRegistrationStatus(Intent intent) {
        String accountID = intent.getStringExtra(PARAM_ACCOUNT_ID);

        if (!mStarted || mActiveSipAccounts.get(accountID) == null) {
            mBroadcastEmitter.registrationState("", 400);
            return;
        }

        SipAccount account = mActiveSipAccounts.get(accountID);
        try {
            mBroadcastEmitter.registrationState(accountID, account.getInfo().getRegStatus());
        } catch (Exception exc) {
            Logger.error(TAG, "Error while getting registration status for " + getValue(getApplicationContext(), accountID), exc);
        }
    }

    private void handleSetDND(Intent intent) {
        boolean dnd = intent.getBooleanExtra(PARAM_DND, false);
        mSharedPreferencesHelper.setDND(dnd);
    }

    public boolean isDND() {
        return mSharedPreferencesHelper.isDND();
    }

    /***   Sip Stack Management    ***/

    private void loadNativeLibraries() {
        try {
            System.loadLibrary("c++_shared");
            Logger.debug(TAG, "libc++_shared loaded");
        } catch (UnsatisfiedLinkError error) {
            Logger.error(TAG, "Error while loading libc++_shared native library", error);
            throw new RuntimeException(error);
        }

        try {
            System.loadLibrary("openh264");
            Logger.debug(TAG, "OpenH264 loaded");
        } catch (UnsatisfiedLinkError error) {
            Logger.error(TAG, "Error while loading OpenH264 native library", error);
            throw new RuntimeException(error);
        }

        try {
            System.loadLibrary("pjsua2");
            Logger.debug(TAG, "PJSIP pjsua2 loaded");
        } catch (UnsatisfiedLinkError error) {
            Logger.error(TAG, "Error while loading PJSIP pjsua2 native library", error);
            throw new RuntimeException(error);
        }
    }

    /**
     * Starts PJSIP Stack.
     */
    private void startStack() {

        if (mStarted) return;

        try {
            Logger.debug(TAG, "Starting PJSIP");
            mEndpoint = new SipEndpoint(this);
            mEndpoint.libCreate();

            EpConfig epConfig = new EpConfig();
            epConfig.getUaConfig().setUserAgent(AGENT_NAME);
            epConfig.getMedConfig().setHasIoqueue(true);
            epConfig.getMedConfig().setClockRate(16000);
            epConfig.getMedConfig().setQuality(10);
            epConfig.getMedConfig().setEcOptions(1);
            epConfig.getMedConfig().setEcTailLen(200);
            epConfig.getMedConfig().setThreadCnt(2);

            //TODO: Keep watch
            /*final StringVector stun_servers = new StringVector();
            final UaConfig uaConfig = epConfig.getUaConfig();
            uaConfig.setStunServer(stun_servers);
            if (own_worker_thread) {
                ua_cfg.setThreadCnt(0);
                ua_cfg.setMainThreadOnly(true);
            }*/

            SipServiceUtils.setSipLogger(epConfig);
            mEndpoint.libInit(epConfig);

            /*TransportConfig udpTransport = new TransportConfig();
            udpTransport.setQosType(pj_qos_type.PJ_QOS_TYPE_VOICE);*/
            TransportConfig tcpTransport = new TransportConfig();
            tcpTransport.setQosType(pj_qos_type.PJ_QOS_TYPE_VOICE);
            /*TransportConfig tlsTransport = new TransportConfig();
            tlsTransport.setQosType(pj_qos_type.PJ_QOS_TYPE_VOICE);
            SipTlsUtils.setTlsConfig(this, mSharedPreferencesHelper.isVerifySipServerCert(), tlsTransport);*/

            //mEndpoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, udpTransport);
            mEndpoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TCP, tcpTransport);
            //mEndpoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TLS, tlsTransport);
            mEndpoint.libStart();

            ArrayList<CodecPriority> codecPriorities = getConfiguredCodecPriorities();
            SipServiceUtils.setAudioCodecPriorities(codecPriorities, mEndpoint);

            SipServiceUtils.setVideoCodecPriorities(mEndpoint);

            Logger.debug(TAG, "PJSIP started!");
            mStarted = true;
            mBroadcastEmitter.stackStatus(true);

        } catch (Exception exc) {
            Logger.error(TAG, "Error while starting PJSIP", exc);
            mStarted = false;
        }
    }

    /**
     * Shuts down PJSIP Stack
     */
    private void stopStack() {

        if (!mStarted) return;

        try {
            Logger.debug(TAG, "Stopping PJSIP");

            /*
             * Do not remove accounts on service stop anymore
             * They should have already been removed (unregistered)
             * In case they have not, it is ok, it means app has been just killed
             * or service force stopped
             *
             * *************************************
             * removeAllActiveAccounts();
             * *************************************
             */

            /* Try to force GC to do its job before destroying the library
             * since it's recommended to do that by PJSUA examples
             */
            Runtime.getRuntime().gc();

            mEndpoint.libDestroy(pjsua_destroy_flag.PJSUA_DESTROY_NO_NETWORK);
            mEndpoint.delete();
            mEndpoint = null;

            Logger.debug(TAG, "PJSIP stopped");
            mBroadcastEmitter.stackStatus(false);

        } catch (Exception exc) {
            Logger.error(TAG, "Error while stopping PJSIP", exc);

        } finally {
            mStarted = false;
            mEndpoint = null;
        }
    }

    private ArrayList<CodecPriority> getCodecPriorityList() {
        startStack();

        if (!mStarted) {
            Logger.error(TAG, "Can't get codec priority list! The SIP Stack has not been " +
                    "initialized! Add an account first!");
            return null;
        }

        try {
            CodecInfoVector2 codecs = mEndpoint.codecEnum2();
            if (codecs == null || codecs.size() == 0) return null;

            ArrayList<CodecPriority> codecPrioritiesList = new ArrayList<>(codecs.size());

            for (int i = 0; i < codecs.size(); i++) {
                CodecInfo codecInfo = codecs.get(i);
                CodecPriority newCodec = new CodecPriority(codecInfo.getCodecId(),
                        codecInfo.getPriority());
                if (!codecPrioritiesList.contains(newCodec))
                    codecPrioritiesList.add(newCodec);
                codecInfo.delete();
            }

            codecs.delete();

            Collections.sort(codecPrioritiesList);
            return codecPrioritiesList;

        } catch (Exception exc) {
            Logger.error(TAG, "Error while getting codec priority list!", exc);
            return null;
        }
    }

    private void handleGetCodecPriorities() {
        ArrayList<CodecPriority> codecs = getCodecPriorityList();

        if (codecs != null) {
            mBroadcastEmitter.codecPriorities(codecs);
        }
    }

    private void handleSetCodecPriorities(Intent intent) {
        ArrayList<CodecPriority> codecPriorities = intent.getParcelableArrayListExtra(PARAM_CODEC_PRIORITIES);

        if (codecPriorities == null) {
            return;
        }

        startStack();

        if (!mStarted) {
            mBroadcastEmitter.codecPrioritiesSetStatus(false);
            return;
        }

        try {
            StringBuilder log = new StringBuilder();
            log.append("Codec priorities successfully set. The priority order is now:\n");

            for (CodecPriority codecPriority : codecPriorities) {
                mEndpoint.codecSetPriority(codecPriority.getCodecId(), (short) codecPriority.getPriority());
                log.append(codecPriority).append(",");
            }

            persistConfiguredCodecPriorities(codecPriorities);
            Logger.debug(TAG, log.toString());
            mBroadcastEmitter.codecPrioritiesSetStatus(true);

        } catch (Exception exc) {
            Logger.error(TAG, "Error while setting codec priorities", exc);
            mBroadcastEmitter.codecPrioritiesSetStatus(false);
        }
    }

    @SuppressWarnings("unused")
    private void removeAllActiveAccounts() {
        if (!mActiveSipAccounts.isEmpty()) {
            for (String accountID : mActiveSipAccounts.keySet()) {
                try {
                    removeAccount(accountID);
                } catch (Exception exc) {
                    Logger.error(TAG, "Error while removing " + getValue(getApplicationContext(), accountID));
                }
            }
        }
    }

    private void addAllConfiguredAccounts() {
        if (!mConfiguredAccounts.isEmpty()) {
            for (SipAccountData accountData : mConfiguredAccounts) {
                try {
                    addAccount(accountData);
                } catch (Exception exc) {
                    Logger.error(TAG, "Error while adding " + getValue(getApplicationContext(), accountData.getIdUri(getApplicationContext())));
                }
            }
        }
    }

    /**
     * Adds a new SIP Account and performs initial registration.
     *
     * @param account SIP account to add
     */
    private void addAccount(SipAccountData account) throws Exception {
        String accountString = account.getIdUri(getApplicationContext());

        SipAccount sipAccount = mActiveSipAccounts.get(accountString);

        if (sipAccount == null || !sipAccount.isValid() || !account.equals(sipAccount.getData())) {
            if (mActiveSipAccounts.containsKey(accountString) && sipAccount != null) {
                sipAccount.delete();
            }
            startStack();
            Logger.debug(TAG, "ServiceInstance:" + this);
            SipAccount pjSipAndroidAccount = new SipAccount(this, account);
            pjSipAndroidAccount.create();
            mActiveSipAccounts.put(accountString, pjSipAndroidAccount);
            Logger.debug(TAG, "SIP account " + getValue(getApplicationContext(), account.getIdUri(getApplicationContext())) + " successfully added");
        }
//        else {
//            sipAccount.setRegistration(true);
//        }
    }

    /**
     * Removes a SIP Account and performs un-registration.
     */
    private void removeAccount(String accountID) {
        SipAccount account = mActiveSipAccounts.remove(accountID);

        if (account == null) {
            Logger.error(TAG, "No account for ID: " + getValue(getApplicationContext(), accountID));
            return;
        }

        Logger.debug(TAG, "Removing SIP account " + getValue(getApplicationContext(), accountID));
        account.delete();
        Logger.debug(TAG, "SIP account " + getValue(getApplicationContext(), accountID) + " successfully removed");
        mBroadcastEmitter.removeAccount(accountID);
    }

    private void persistConfiguredAccounts() {
        mSharedPreferencesHelper.persistConfiguredAccounts(mConfiguredAccounts);
    }

    private void persistConfiguredCodecPriorities(ArrayList<CodecPriority> codecPriorities) {
        mSharedPreferencesHelper.persistConfiguredCodecPriorities(codecPriorities);
    }

    private void loadConfiguredAccounts() {
        mConfiguredAccounts = mSharedPreferencesHelper.retrieveConfiguredAccounts();
    }

    private ArrayList<CodecPriority> getConfiguredCodecPriorities() {
        return mSharedPreferencesHelper.retrieveConfiguredCodecPriorities();
    }

    protected synchronized AudDevManager getAudDevManager() {
        return mEndpoint.audDevManager();
    }

    protected synchronized VidDevManager getVidDevManager() {
        return mEndpoint.vidDevManager();
    }

    protected BroadcastEventEmitter getBroadcastEmitter() {
        return mBroadcastEmitter;
    }

    public static ConcurrentHashMap<String, SipAccount> getActiveSipAccounts() {
        return mActiveSipAccounts;
    }

    public static SipAccount getActiveSipAccount(final String accountID) {
        return mActiveSipAccounts.get(accountID);
    }

    public static SipAccount getActiveSipAccount(Context context) {
        return mActiveSipAccounts.get(SharedPreferencesHelper.getInstance(context).getAccountID());
    }

    public void removeGuestAccount() {
        removeAccount(mConfiguredGuestAccount.getIdUri(getApplicationContext()));
        mConfiguredGuestAccount = null;
    }

    private void startForeground(final Notification notification) {
        startForeground(SERVICE_FOREGROUND_NOTIFICATION_ID, notification);
    }

}
