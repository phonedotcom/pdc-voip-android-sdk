package com.phone.sip;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.view.Surface;

import com.phone.sip.constants.CallEvent;
import com.phone.sip.constants.CallMediaEvent;
import com.phone.sip.constants.SipServiceConstants;

import org.pjsip.pjsua2.AudDevManager;
import org.pjsip.pjsua2.AudioMedia;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.CallSetting;
import org.pjsip.pjsua2.CallVidSetStreamParam;
import org.pjsip.pjsua2.Media;
import org.pjsip.pjsua2.OnCallMediaEventParam;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallStateParam;
import org.pjsip.pjsua2.OnStreamDestroyedParam;
import org.pjsip.pjsua2.RtcpStreamStat;
import org.pjsip.pjsua2.StreamInfo;
import org.pjsip.pjsua2.StreamStat;
import org.pjsip.pjsua2.VideoPreview;
import org.pjsip.pjsua2.VideoPreviewOpParam;
import org.pjsip.pjsua2.VideoWindow;
import org.pjsip.pjsua2.VideoWindowHandle;
import org.pjsip.pjsua2.pjmedia_event_type;
import org.pjsip.pjsua2.pjmedia_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_role_e;
import org.pjsip.pjsua2.pjsip_status_code;
import org.pjsip.pjsua2.pjsua2;
import org.pjsip.pjsua2.pjsua_call_flag;
import org.pjsip.pjsua2.pjsua_call_media_status;
import org.pjsip.pjsua2.pjsua_call_vid_strm_op;

/**
 * Wrapper around PJSUA2 Call object.
 *
 * @author gotev (Aleksandar Gotev)
 */
@SuppressWarnings("unused")
public class SipCall extends Call implements ICall {

    private static final String LOG_TAG = SipCall.class.getSimpleName();

    private final SipAccount account;
    private boolean localHold = false;
    private boolean localMute = false;
    private boolean localVideoMute = false;
    private long connectTimestamp = 0;
    private ToneGenerator toneGenerator;
    private boolean videoCall = false;
    private boolean videoConference = false;
    private boolean frontCamera = true;

    private VideoWindow mVideoWindow;
    private VideoPreview mVideoPreview;

    private StreamInfo streamInfo = null;
    private StreamStat streamStat = null;

    /**
     * Incoming call constructor.
     *
     * @param account the account which own this call
     * @param callID  the id of this call
     */
    public SipCall(SipAccount account, int callID) {
        super(account, callID);
        this.account = account;
        mVideoPreview = null;
        mVideoWindow = null;
    }

    /**
     * Outgoing call constructor.
     *
     * @param account account which owns this call
     */
    public SipCall(SipAccount account) {
        super(account);
        this.account = account;
    }

    public int getCurrentState() {
        try {
            CallInfo info = getInfo();
            return info.getState();
        } catch (Exception exc) {
            Logger.error(getClass().getSimpleName(), "Error while getting call Info", exc);
            return pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED;
        }
    }

    @Override
    public void onCallState(OnCallStateParam prm) {
        Logger.debug(LOG_TAG, "onCallState()");
        try {
            CallInfo info = getInfo();
            int callID = info.getId();
            int callState = info.getState();
            int callStatus = pjsip_status_code.PJSIP_SC_NULL;
            Logger.debug(LOG_TAG, "onCallState() -> Try");
            Logger.debug(LOG_TAG, "onCallState() -> Try -> callState : " +callState);
            Logger.debug(LOG_TAG, "onCallState() -> Try -> callStatus 1: "+callStatus);

            /*
             * From: http://www.pjsip.org/docs/book-latest/html/call.html#call-disconnection
             *
             * Call disconnection event is a special event since once the callback that
             * reports this event returns, the call is no longer valid and any operations
             * invoked to the call object will raise error exception.
             * Thus, it is recommended to delete the call object inside the callback.
             */

            CallEvent events = null;

            try {
                callStatus = info.getLastStatusCode();
                Logger.debug(LOG_TAG, "onCallState() -> Try -> callStatus 2: "+callStatus);
                account.getService().setLastCallStatus(callStatus);
            } catch (Exception ex) {
                Logger.error(LOG_TAG, "Error while getting call status", ex);
            }

            if (callState == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                checkAndStopLocalRingBackTone();
                //stopVideoFeeds();
                //stopSendingKeyFrame();
                account.removeCall(callID);

                events = CallEvent.DISCONNECTED;

                if (connectTimestamp > 0 && streamInfo != null && streamStat != null) {
                    try {
                        sendCallStats(callID, info.getConnectDuration().getSec(), callStatus);
                    } catch (Exception ex) {
                        Logger.error(LOG_TAG, "Error while sending call stats", ex);
                        throw ex;
                    }
                }

                if(account.isActiveCallPresent()) {
                    account.getService().stopForegroundService(account);
                }

            } else if (callState == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                Logger.debug(LOG_TAG, "PJSIP_INV_STATE_CONFIRMED");

                checkAndStopLocalRingBackTone();
                connectTimestamp = System.currentTimeMillis();
                if (videoCall) {
                    //setVideoMute(false);
                    //startSendingKeyFrame();
                    account.getService().getBroadcastEmitter().callState(CallEvent.VIDEO_INITIATED);
                }

                events = CallEvent.ONGOING_CALL;

                // check whether the 183 has arrived or not
            } else if (callState == pjsip_inv_state.PJSIP_INV_STATE_EARLY) {
                int statusCode = info.getLastStatusCode();
                // check if 180 && call is outgoing (ROLE UAC)
                if (statusCode == pjsip_status_code.PJSIP_SC_RINGING && info.getRole() == pjsip_role_e.PJSIP_ROLE_UAC) {
                    checkAndStopLocalRingBackTone();
                    toneGenerator = new ToneGenerator(AudioManager.STREAM_VOICE_CALL, 100);
                    toneGenerator.startTone(ToneGenerator.TONE_SUP_RINGTONE);
                    // check if 183

                    events = CallEvent.PLAY_RINGTONE;
                } else if (statusCode == pjsip_status_code.PJSIP_SC_PROGRESS) {
                    checkAndStopLocalRingBackTone();
                }
            }

            if(events!= null)
                account.getService().getBroadcastEmitter().callState(events);

            if (callState == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                account.getService().setLastCallStatus(0);
                //delete();
                if(account.noActiveCallPresent()){
                    account.getService().stopForegroundService(account);
                }
            }
        } catch (Exception exc) {
            Logger.error(LOG_TAG, "onCallState: error while getting call info", exc);
        }
        Runtime.getRuntime().gc();
    }

    @Override
    public void onCallMediaState(OnCallMediaStateParam prm) {
        Logger.debug(LOG_TAG, "onCallMediaState()");
        CallInfo info;
        try {
            info = getInfo();
        } catch (Exception exc) {
            Logger.error(LOG_TAG, "onCallMediaState: error while getting call info", exc);
            return;
        }

        for (int i = 0; i < info.getMedia().size(); i++) {
            Media media = getMedia(i);
            CallMediaInfo mediaInfo = info.getMedia().get(i);

            if (mediaInfo.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO
                    && media != null
                    && mediaInfo.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE) {

                handleAudioMedia(media);

            } else if (mediaInfo.getType() == pjmedia_type.PJMEDIA_TYPE_VIDEO
                    && mediaInfo.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE
                    && mediaInfo.getVideoIncomingWindowId() != pjsua2.INVALID_ID) {

                handleVideoMedia(mediaInfo);
            }
        }
    }

    @Override
    public void onCallMediaEvent(OnCallMediaEventParam prm) {
        if (prm.getEv().getType() == pjmedia_event_type.PJMEDIA_EVENT_FMT_CHANGED) {
            Logger.debug(LOG_TAG, "onCallMediaEvent()");
            // Sending new video size
            try {
                account.getService().getBroadcastEmitter().videoSize(
                        (int) mVideoWindow.getInfo().getSize().getW(),
                        (int) mVideoWindow.getInfo().getSize().getH());
                account.getService().getBroadcastEmitter().callMediaEvent(CallMediaEvent.FORMAT_CHANGED);
//                account.getService().getBroadcastEmitter().callMediaEvent(prm.getEv().getType());
            } catch (Exception ex) {
                Logger.error(LOG_TAG, "Unable to get video dimensions", ex);
            }
            super.onCallMediaEvent(prm);
        }
    }

    @Override
    public void onStreamDestroyed(OnStreamDestroyedParam prm) {
        long idx = prm.getStreamIdx();
        try {
            CallInfo callInfo = getInfo();
            if (getInfo().getMedia().get((int) idx).getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO) {
                streamInfo = getStreamInfo(idx);
                streamStat = getStreamStat(idx);
            }
        } catch (Exception ex) {
            Logger.error(LOG_TAG, "onStreamDestroyed: error while getting call stats", ex);
        }
        super.onStreamDestroyed(prm);
    }

    /**
     * Get the total duration of the call.
     *
     * @return the duration in milliseconds or 0 if the call is not connected.
     */
    public long getConnectTimestamp() {
        return connectTimestamp;
    }

    public void acceptIncomingCall() {
        CallOpParam param = new CallOpParam();
        param.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
        setMediaParams(param);
        if (!videoCall) {
            CallSetting callSetting = param.getOpt();
            callSetting.setFlag(pjsua_call_flag.PJSUA_CALL_INCLUDE_DISABLED_MEDIA);
        }
        try {
            answer(param);
        } catch (Exception exc) {
            Logger.error(LOG_TAG, "Failed to accept incoming call", exc);
        }
    }

    public void sendBusyHereToIncomingCall() {
        CallOpParam param = new CallOpParam();
        param.setStatusCode(pjsip_status_code.PJSIP_SC_BUSY_HERE);

        try {
            answer(param);
        } catch (Exception exc) {
            Logger.error(LOG_TAG, "Failed to send busy here", exc);
        }
    }

    public void hangUp() throws Exception {
        CallOpParam param = new CallOpParam();
        param.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);

        try {
            hangup(param);
        } catch (Exception exc) {
            Logger.error(LOG_TAG, "Failed to hangUp call", exc);
            throw exc;
        }
    }

    /**
     * Utility method to mute/unmute the device microphone during a call.
     *
     * @param mute true to mute the microphone, false to un-mute it
     */
    public void setMute(boolean mute) throws Exception {
        // return immediately if we are not changing the current state
        if (localMute == mute) return;

        CallInfo info;
        try {
            info = getInfo();
        } catch (Exception exc) {
            Logger.error(LOG_TAG, "setMute: error while getting call info", exc);
            throw exc;
        }

        for (int i = 0; i < info.getMedia().size(); i++) {
            Media media = getMedia(i);
            CallMediaInfo mediaInfo = info.getMedia().get(i);

            if (mediaInfo.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO
                    && media != null
                    && mediaInfo.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE) {
                AudioMedia audioMedia = AudioMedia.typecastFromMedia(media);

                // connect or disconnect the captured audio
                try {
                    AudDevManager mgr = account.getService().getAudDevManager();
                    if (mute) mgr.getCaptureDevMedia().stopTransmit(audioMedia);
                    else mgr.getCaptureDevMedia().startTransmit(audioMedia);
                    localMute = mute;
                    account.getService().getBroadcastEmitter().callMediaState(
                            account.getData().getIdUri(account.getService().getApplicationContext()), getId(), MediaState.LOCAL_MUTE, localMute);
                } catch (Exception exc) {
                    Logger.error(LOG_TAG, "setMute: error while connecting audio media to sound device", exc);
                    throw exc;
                }
            }
        }
    }

    public boolean isLocalMute() {
        return localMute;
    }

    public void toggleMute() throws Exception {
        setMute(!localMute);
    }

    /**
     * Utility method to transfer a call to a number in the same realm as the account to
     * which this call belongs to. If you want to transfer the call to a different realm, you
     * have to pass the full string in this format: sip:NUMBER@REALM. E.g. sip:200@mycompany.com
     *
     * @param destination destination to which to transfer the call.
     * @throws Exception if an error occurs during the call transfer
     */
    public void transferTo(String destination) throws Exception {
        String transferString;

        if (destination.startsWith("sip:")) {
            transferString = "<" + destination + ">";
        } else {
            if ("*".equals(account.getData().getRealm())) {
                transferString = "<sip:" + destination + ">";
            } else {
                transferString = "<sip:" + destination + "@" + account.getData().getRealm() + ">";
            }
        }

        CallOpParam param = new CallOpParam();

        xfer(transferString, param);
    }

    public boolean setHold(boolean hold) throws Exception {
        // return immediately if we are not changing the current state
        if (localHold == hold) return hold;

        CallOpParam param = new CallOpParam();

        try {
            if (hold) {
                Logger.debug(LOG_TAG, "holding call with ID " + getId());
                setHold(param);
            } else {
                // http://lists.pjsip.org/pipermail/pjsip_lists.pjsip.org/2015-March/018246.html
                Logger.debug(LOG_TAG, "un-holding call with ID " + getId());
                setMediaParams(param);
                CallSetting opt = param.getOpt();
                opt.setFlag(pjsua_call_flag.PJSUA_CALL_UNHOLD);
                reinvite(param);
            }
            localHold = hold;
            account.getService().getBroadcastEmitter().callMediaState(
                    account.getData().getIdUri(account.getService().getApplicationContext()), getId(), MediaState.LOCAL_HOLD, localHold);
            return hold;
        } catch (Exception exc) {
            String operation = hold ? "hold" : "unhold";
            Logger.error(LOG_TAG, "Error while trying to " + operation + " call", exc);
            throw new Exception("Error while trying to " + operation + " call \n + exc");
        }
    }

    public boolean toggleHold() throws Exception {
        return setHold(!localHold);
    }

    public boolean isLocalHold() {
        return localHold;
    }

    // check if Local RingBack Tone has started, if so, stop it.
    private void checkAndStopLocalRingBackTone() {
        if (toneGenerator != null) {
            toneGenerator.stopTone();
            toneGenerator.release();
            toneGenerator = null;
        }
    }

    // disable video programmatically
    @Override
    public void makeCall(String dst_uri, CallOpParam prm) throws java.lang.Exception {
        Logger.debug(LOG_TAG, "makeCall()");
        setMediaParams(prm);
        if (!videoCall) {
            CallSetting callSetting = prm.getOpt();
            callSetting.setFlag(pjsua_call_flag.PJSUA_CALL_INCLUDE_DISABLED_MEDIA);
        }
        super.makeCall(dst_uri, prm);
    }

    private void handleAudioMedia(Media media) {
        Logger.debug(LOG_TAG, "handleAudioMedia()");
        AudioMedia audioMedia = AudioMedia.typecastFromMedia(media);

        // connect the call audio media to sound device
        try {
            AudDevManager audDevManager = account.getService().getAudDevManager();
            if (audioMedia != null) {
                try {
                    audioMedia.adjustRxLevel((float) 1.5);
                    audioMedia.adjustTxLevel((float) 1.5);
                } catch (Exception exc) {
                    Logger.error(LOG_TAG, "Error while adjusting levels", exc);
                }

                audioMedia.startTransmit(audDevManager.getPlaybackDevMedia());
                audDevManager.getCaptureDevMedia().startTransmit(audioMedia);
            }
        } catch (Exception exc) {
            Logger.error(LOG_TAG, "Error while connecting audio media to sound device", exc);
        }
    }

    private void handleVideoMedia(CallMediaInfo mediaInfo) {
        if (mVideoWindow != null) {
            mVideoWindow.delete();
        }
        if (mVideoPreview != null) {
            mVideoPreview.delete();
        }
        if (!videoConference) {
            // Since 2.9 pjsip will not start capture device if autoTransmit is false
            // thus mediaInfo.getVideoCapDev() always returns -3 -> NULL
            // mVideoPreview = new VideoPreview(mediaInfo.getVideoCapDev());
            mVideoPreview = new VideoPreview(SipServiceConstants.FRONT_CAMERA_CAPTURE_DEVICE);
        }
        mVideoWindow = new VideoWindow(mediaInfo.getVideoIncomingWindowId());
    }

    public VideoWindow getVideoWindow() {
        return mVideoWindow;
    }

    public void setVideoWindow(VideoWindow mVideoWindow) {
        this.mVideoWindow = mVideoWindow;
    }

    public VideoPreview getVideoPreview() {
        return mVideoPreview;
    }

    public void setVideoPreview(VideoPreview mVideoPreview) {
        this.mVideoPreview = mVideoPreview;
    }

    private void stopVideoFeeds() {
        stopIncomingVideoFeed();
        stopPreviewVideoFeed();
    }

    public void setIncomingVideoFeed(Surface surface) {
        Logger.debug(LOG_TAG, "setIncomingVideoFeed()");
        if (mVideoWindow != null) {
            VideoWindowHandle videoWindowHandle = new VideoWindowHandle();
            videoWindowHandle.getHandle().setWindow(surface);
            try {
                mVideoWindow.setWindow(videoWindowHandle);
                /*account.getService().getBroadcastEmitter().videoSize(
                        (int) mVideoWindow.getInfo().getSize().getW(),
                        (int) mVideoWindow.getInfo().getSize().getH());*/

                // start video again if not mute
                setVideoMute(localVideoMute);
            } catch (Exception ex) {
                Logger.error(LOG_TAG, "Unable to setup Incoming Video Feed", ex);
            }
        }
    }

    public void startPreviewVideoFeed(Surface surface) {
        if (mVideoPreview != null) {
            VideoWindowHandle videoWindowHandle = new VideoWindowHandle();
            videoWindowHandle.getHandle().setWindow(surface);
            VideoPreviewOpParam videoPreviewOpParam = new VideoPreviewOpParam();
            videoPreviewOpParam.setWindow(videoWindowHandle);
            try {
                mVideoPreview.start(videoPreviewOpParam);
            } catch (Exception ex) {
                Logger.error(LOG_TAG, "Unable to start Video Preview", ex);
            }
        }
    }

    public void stopIncomingVideoFeed() {
        VideoWindow videoWindow = getVideoWindow();
        if (videoWindow != null) {
            try {
                videoWindow.delete();
            } catch (Exception ex) {
                Logger.error(LOG_TAG, "Unable to stop remote video feed", ex);
            }
        }
    }

    public void stopPreviewVideoFeed() {
        VideoPreview videoPreview = getVideoPreview();
        if (videoPreview != null) {
            try {
                videoPreview.stop();
            } catch (Exception ex) {
                Logger.error(LOG_TAG, "Unable to stop preview video feed", ex);
            }
        }
    }

    public boolean isVideoCall() {
        return videoCall;
    }

    public boolean isVideoConference() {
        return videoConference;
    }

    public void setVideoParams(boolean videoCall, boolean videoConference) {
        this.videoCall = videoCall;
        this.videoConference = videoConference;
    }

    public void setVideoParam(boolean videoCall) {
        this.videoCall = videoCall;
    }

    private void setMediaParams(CallOpParam param) {
        CallSetting callSetting = param.getOpt();
        callSetting.setAudioCount(1);
        callSetting.setVideoCount(videoCall ? 1 : 0);
    }

    public void setVideoMute(boolean videoMute) throws Exception {
        try {
            vidSetStream(videoMute
                            ? pjsua_call_vid_strm_op.PJSUA_CALL_VID_STRM_STOP_TRANSMIT
                            : pjsua_call_vid_strm_op.PJSUA_CALL_VID_STRM_START_TRANSMIT,
                    new CallVidSetStreamParam());
            localVideoMute = videoMute;
            account.getService().getBroadcastEmitter().callMediaState(
                    account.getData().getIdUri(account.getService().getApplicationContext()), getId(), MediaState.LOCAL_VIDEO_MUTE, localVideoMute);
        } catch (Exception ex) {
            Logger.error(LOG_TAG, "Error while toggling video transmission", ex);
            throw ex;
        }
    }

    public boolean isLocalVideoMute() {
        return localVideoMute;
    }

    public boolean isFrontCamera() {
        return frontCamera;
    }

    public void setFrontCamera(boolean frontCamera) {
        this.frontCamera = frontCamera;
    }

    private final Runnable sendKeyFrameRunnable = () -> {
        Logger.debug(LOG_TAG, "sendKeyFrameRunnable - Runnable");
        try {
            vidSetStream(pjsua_call_vid_strm_op.PJSUA_CALL_VID_STRM_SEND_KEYFRAME, new CallVidSetStreamParam());
        } catch (Exception ex) {
            Logger.error(LOG_TAG, "Error sending periodic keyframe", ex);
        } finally {
            startSendingKeyFrame();
        }
    };

    private void startSendingKeyFrame() {
        account.getService().enqueueDelayedJob(sendKeyFrameRunnable, SipServiceConstants.DELAYED_JOB_DEFAULT_DELAY);
    }

    private void stopSendingKeyFrame() {
        account.getService().dequeueJob(sendKeyFrameRunnable);
    }

    private void sendCallStats(int callID, int duration, int callStatus) {
        String audioCodec = streamInfo.getCodecName().toLowerCase() + "_" + streamInfo.getCodecClockRate();

        RtcpStreamStat rxStat = streamStat.getRtcp().getRxStat();
        RtcpStreamStat txStat = streamStat.getRtcp().getTxStat();

        Jitter rxJitter = new Jitter(
                rxStat.getJitterUsec().getMax(),
                rxStat.getJitterUsec().getMean(),
                rxStat.getJitterUsec().getMin());

        Jitter txJitter = new Jitter(
                txStat.getJitterUsec().getMax(),
                txStat.getJitterUsec().getMean(),
                txStat.getJitterUsec().getMin());

        RtpStreamStats rx = new RtpStreamStats(
                (int) rxStat.getPkt(),
                (int) rxStat.getDiscard(),
                (int) rxStat.getLoss(),
                (int) rxStat.getReorder(),
                (int) rxStat.getDup(),
                rxJitter
        );

        RtpStreamStats tx = new RtpStreamStats(
                (int) txStat.getPkt(),
                (int) txStat.getDiscard(),
                (int) txStat.getLoss(),
                (int) txStat.getReorder(),
                (int) txStat.getDup(),
                txJitter
        );

        account.getService().getBroadcastEmitter().callStats(callID, duration, audioCodec, callStatus, rx, tx);
        streamInfo = null;
        streamStat = null;
    }

    /**
     * Method for returning the CallerName, uses {@link #getNumber()}, it performs operation on
     * {@link #getNumber()} to return name based on formatting and custom logic in implementatio
     * class.
     *
     * @return the callerName
     */
    @Override
    public String getCallerName() {
        return null;
    }

    /**
     * Method for returning the CallerName
     *
     * @return the caller name
     * @see #getCallerName()
     */
    @Override
    public String getNumber() {
        return null;
    }

    /**
     * Method for setting the caller name
     *
     * @param callerNumber
     */
    @Override
    public void setNumber(String callerNumber) {

    }

    /**
     * Method for checking the hold unhold status of current call
     *
     * @return boolean indicating whether the current call is on hold
     * if true the call is on hold
     * else not
     */
    @Override
    public boolean isHoldCall() {
        return false;
    }

    /**
     * Method for setting the hold/ unhold status if current call
     *
     * @param isHoldCall boolean true-> call is on hold
     *                   false -> call in not on hold
     */
    @Override
    public void setHoldCall(boolean isHoldCall) {

    }

    /**
     * Method for checking whether the current call is same as other one by comparing their call id
     *
     * @param callId callID to match
     * @return boolean true if both the calls match else false
     */
    @Override
    public boolean isCallIdPresent(int callId) {
        return false;
    }

    /**
     * Method to get the state of call
     *
     * @return the current state of call
     * @see CallState
     */
    @Override
    public CallState getState() {
        return null;
    }

    /**
     * Method for setting the state of call
     *
     * @param connected indicates the state of call
     * @see CallState
     */
    @Override
    public void setState(CallState connected) {

    }

    /**
     * Method to set the active status of call
     *
     * @param active if true call is active else not
     */
    @Override
    public void setActive(boolean active) {

    }

    @Override
    public String getImageUrl() {
        return null;
    }

    /**
     * @return
     */
    @Override
    public String getLinkedUUID() {
        return null;
    }

    @Override
    public void setLinkedUUID(String linkedUUID) {

    }

    /**
     * Method for setting the time of call
     *
     * @param time long time of call
     */
    @Override
    public void setTime(long time) {

    }

    /**
     * Method for getting the time of call
     *
     * @return the time of call in long
     */
    @Override
    public long getTime() {
        return 0;
    }

    /**
     * Method to set the type of call
     *
     * @param callType the type of call
     * @see CallType
     */
    @Override
    public void setCallType(CallType callType) {

    }

    /**
     * Method to retrieve the type of call
     *
     * @return the type of call
     * @see CallType
     */
    @Override
    public CallType getCallType() {
        return null;
    }

    @Override
    public boolean isCallOnMute() {
        return false;
    }
}
