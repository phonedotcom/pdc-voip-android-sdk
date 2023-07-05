package net.gotev.sipservice;

import static net.gotev.sipservice.ObfuscationHelper.getValue;

import android.os.Build;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnRegStateParam;
import org.pjsip.pjsua2.SipHeader;
import org.pjsip.pjsua2.SipHeaderVector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Wrapper around PJSUA2 Account object.
 *
 * @author gotev (Aleksandar Gotev)
 */
public class SipAccount extends Account {

    private static final String LOG_TAG = SipAccount.class.getSimpleName();

    private final HashMap<Integer, SipCall> activeCalls = new HashMap<>();
    private final SipAccountData data;
    private final SipService service;
    private boolean isGuest = false;
    private ICall activeIncomingCall;

    protected SipAccount(SipService service, SipAccountData data) {
        super();
        this.service = service;
        this.data = data;
    }

    public SipService getService() {
        return service;
    }

    public SipAccountData getData() {
        return data;
    }

    public void create() throws Exception {
        create(data.getAccountConfig(service.getApplicationContext()));
    }

    public void createGuest() throws Exception {
        isGuest = true;
        create(data.getGuestAccountConfig());
    }

    protected void removeCall(int callId) {
        SipCall call = activeCalls.get(callId);

        if (call != null) {
            Logger.debug(LOG_TAG, "Removing call with ID: " + callId);
            activeCalls.remove(callId);
        }

        if (isGuest) {
            service.removeGuestAccount();
            delete();
        }
    }

    public SipCall getCall(int callId) {
        return activeCalls.get(callId);
    }

    public Set<Integer> getCallIDs() {
        return activeCalls.keySet();
    }

    public SipCall addIncomingCall(int callId) {

        SipCall call = new SipCall(this, callId);
        activeCalls.put(callId, call);
        Logger.debug(LOG_TAG, "Added incoming call with ID " + callId
                + " to " + getValue(service.getApplicationContext(), data.getIdUri(service.getApplicationContext()))
        );
        return call;
    }

    public SipCall addOutgoingCall(final String numberToDial, boolean isVideo, boolean isVideoConference, boolean isTransfer) {

        SipCall call = new SipCall(this);
        call.setVideoParams(isVideo, isVideoConference);

        CallOpParam callOpParam = new CallOpParam();
        try {
            if (numberToDial.startsWith("sip:")) {
                call.makeCall(numberToDial, callOpParam);
            } else {
                if ("*".equals(data.getRealm())) {
                    call.makeCall("sip:" + numberToDial, callOpParam);
                } else {
                    call.makeCall("sip:" + numberToDial + "@" + data.getRealm(), callOpParam);
                }
            }
            activeCalls.put(call.getId(), call);
            Logger.debug(LOG_TAG, "New outgoing call with ID: " + call.getId());

            return call;

        } catch (Exception exc) {
            Logger.error(LOG_TAG, "Error while making outgoing call", exc);
            return null;
        }
    }

    public SipCall addOutgoingCall(final String numberToDial) {
        return addOutgoingCall(numberToDial, false, false, false);
    }

    public SipCall addOutgoingForIncomingCall(String numberToDial,
                                              final String slot,
                                              final String server,
                                              final String linkedUuid,
                                              final String callerName,
                                              boolean isVideo) {
        Logger.debug(LOG_TAG, "addOutgoingForIncomingCall()");

        // allow calls only if there are no other ongoing calls
        SipCall call = new SipCall(this);
        call.setVideoParam(isVideo);

        CallOpParam callOpParam = new CallOpParam(true);

        final SipHeader hSlot = new SipHeader();
        hSlot.setHName("X-Slot");
        hSlot.setHValue(slot);

        final SipHeader hServer = new SipHeader();
        hServer.setHName("X-Server");
        hServer.setHValue(server);

        final SipHeaderVector headerVector = new SipHeaderVector();
        headerVector.add(hSlot);
        headerVector.add(hServer);
        callOpParam.getTxOption().setHeaders(headerVector);

        //callOpParam.getOpt().setVideoCount(1);
        //callOpParam.getOpt().setAudioCount(1);

        try {
            //Put Number in SipCall for further use
            call.setNumber(numberToDial);

            Logger.debug(LOG_TAG, "CallerName: " + callerName);
            numberToDial = SipUtility.getSipUserUri(callerName, service.getApplicationContext());
            Logger.debug(LOG_TAG, "Number To Dial: " + numberToDial);

            if (numberToDial.startsWith("sip:")) {
                call.makeCall(numberToDial, callOpParam);
            } else {
                if ("*".equals(data.getRealm())) {
                    call.makeCall("sip:" + numberToDial, callOpParam);
                } else {
                    call.makeCall("sip:" + numberToDial + "@" + data.getRealm(), callOpParam);
                }
            }

            call.setLinkedUUID(linkedUuid);
            call.setState(CallState.CONNECTED);
            call.setCallType(CallType.INCOMING);

            //Set active incoming call null
            activeIncomingCall = null;

            activeCalls.put(call.getId(), call);
            Logger.debug(LOG_TAG, "New outgoing call with ID: " + call.getId());

            return call;

        } catch (Exception exc) {
            Logger.error(LOG_TAG, "Error while making outgoing call", exc);
            return null;
        }
    }

    public void declineIncomingCall(String numberToDial,
                                    final String slot,
                                    final String server,
                                    final String linkedUuid,
                                    boolean isVideo) throws Exception {

        // allow calls only if there are no other ongoing calls
        SipCall call = new SipCall(this);
        call.setVideoParam(isVideo);

        CallOpParam callOpParam = new CallOpParam(true);

        final SipHeader hSlot = new SipHeader();
        hSlot.setHName("X-Slot");
        hSlot.setHValue(slot);

        final SipHeader hServer = new SipHeader();
        hServer.setHName("X-Server");
        hServer.setHValue(server);

        SipHeader hDisconnect = new SipHeader();
        hDisconnect.setHName("X-Disconnect");
        hDisconnect.setHValue("true");

        final SipHeaderVector headerVector = new SipHeaderVector();
        headerVector.add(hSlot);
        headerVector.add(hServer);
        headerVector.add(hDisconnect);
        callOpParam.getTxOption().setHeaders(headerVector);

        try {
            //Put Number in SipCall for further use
            call.setNumber(numberToDial);

            numberToDial = SipUtility.getSipUserUri(numberToDial, service.getApplicationContext());
            Logger.debug(LOG_TAG, "Number To Dial: " + numberToDial);

            if (numberToDial.startsWith("sip:")) {
                call.makeCall(numberToDial, callOpParam);
            } else {
                if ("*".equals(data.getRealm())) {
                    call.makeCall("sip:" + numberToDial, callOpParam);
                } else {
                    call.makeCall("sip:" + numberToDial + "@" + data.getRealm(), callOpParam);
                }
            }

            call.setLinkedUUID(linkedUuid);
            call.setState(CallState.DISCONNECTED);
            call.setCallType(CallType.INCOMING);

        } catch (Exception exc) {
            Logger.error(LOG_TAG, "Error while making sip call", exc);
            throw exc;
        }
    }

    public void rejectIncomingCallUserBusy(String numberToDial,
                                           final String slot,
                                           final String server,
                                           final String linkedUuid,
                                           boolean isVideo,
                                           final String errorCode) throws Exception {

        // allow calls only if there are no other ongoing calls
        SipCall call = new SipCall(this);
        call.setVideoParam(isVideo);

        CallOpParam callOpParam = new CallOpParam(true);

        final SipHeader hSlot = new SipHeader();
        hSlot.setHName("X-Slot");
        hSlot.setHValue(slot);

        final SipHeader hServer = new SipHeader();
        hServer.setHName("X-Server");
        hServer.setHValue(server);

        SipHeader hDisconnect = new SipHeader();
        hDisconnect.setHName("X-Disconnect");
        hDisconnect.setHValue(errorCode);

        final SipHeaderVector headerVector = new SipHeaderVector();
        headerVector.add(hSlot);
        headerVector.add(hServer);
        headerVector.add(hDisconnect);
        callOpParam.getTxOption().setHeaders(headerVector);

        // TODO - in case any issue with this code will have to look into the commented code block below
        /*pjsip_status_code code = currentStatusCode;
        if (account != null) {
            try {
                if (account.isValid()) {
                    //code = account.getInfo().getRegStatus();
                }
            } catch (Exception e) {
                e.printStackTrace();
                handleInactiveAccount();
                isPendingCallToHandleWithErrorCodes = true;
                errorCodes = errorCode;
                activeIncomingCall = incomingcallObj;
                return;
            }
        }
        if (code != null && code.equals(pjsip_status_code.PJSIP_SC_TRYING)) {
            Log.d("navya", "navya PJSIP_SC_TRYING");
            handleInactiveAccount();
            isPendingCallToHandleWithErrorCodes = true;
            errorCodes = errorCode;
            activeIncomingCall = incomingcallObj;
            return;
        }

        if (account == null) {
            handleLogoutAndLogin(false);
            isPendingCallToHandleWithErrorCodes = true;
            errorCodes = errorCode;
            activeIncomingCall = incomingcallObj;
            return;
        }

        if (!account.isValid() || value) {
            handleInactiveAccount();
            isPendingCallToHandleWithErrorCodes = true;
            errorCodes = errorCode;
            activeIncomingCall = incomingcallObj;
            return;
        } else {
            isPendingCallToHandleWithErrorCodes = false;
        }*/
        try {
            numberToDial = SipUtility.getSipUserUri(numberToDial, service.getApplicationContext());
            Logger.debug(LOG_TAG, "Number To Dial: " + numberToDial);

            if (numberToDial.startsWith("sip:")) {
                call.makeCall(numberToDial, callOpParam);
            } else {
                if ("*".equals(data.getRealm())) {
                    call.makeCall("sip:" + numberToDial, callOpParam);
                } else {
                    call.makeCall("sip:" + numberToDial + "@" + data.getRealm(), callOpParam);
                }
            }

            call.setVideoParam(true);
            call.setLinkedUUID(linkedUuid);
            call.setState(CallState.DISCONNECTED);
            call.setCallType(CallType.INCOMING);
        } catch (Exception exc) {
            Logger.error(LOG_TAG, "Error while making sip call", exc);
            throw exc;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SipAccount that = (SipAccount) o;

        return data.equals(that.data);

    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    @Override
    public void onRegState(OnRegStateParam prm) {
        service.getBroadcastEmitter().registrationState(data.getIdUri(service.getApplicationContext()), prm.getCode());
    }

    @Override
    public void onIncomingCall(OnIncomingCallParam prm) {

        System.out.println("======== Incoming call ======== ");

        /*SipCall call = addIncomingCall(prm.getCallId());

        // Send 603 Decline if in DND mode
        if (service.isDND()) {
            try {
                CallerInfo contactInfo = new CallerInfo(call.getInfo());
                service.getBroadcastEmitter().missedCall(contactInfo.getDisplayName(), contactInfo.getRemoteUri());
                call.declineIncomingCall();
                Logger.debug(LOG_TAG, "Decline call with ID: " + prm.getCallId());
            } catch(Exception ex) {
                Logger.error(LOG_TAG, "Error while getting missed call info", ex);
            }
            return;
        }

        // Send 486 Busy Here if there's an already ongoing call
        int totalCalls = 0;
        for (SipAccount _sipAccount: SipService.getActiveSipAccounts().values()) {
            totalCalls += _sipAccount.getCallIDs().size();
        }

        if (totalCalls > 1) {
            try {
                CallerInfo contactInfo = new CallerInfo(call.getInfo());
                service.getBroadcastEmitter().missedCall(contactInfo.getDisplayName(), contactInfo.getRemoteUri());
                call.sendBusyHereToIncomingCall();
                Logger.debug(LOG_TAG, "Sending busy to call ID: " + prm.getCallId());
            } catch(Exception ex) {
                Logger.error(LOG_TAG, "Error while getting missed call info", ex);
            }
            return;
        }

        try {
            // Answer with 180 Ringing
            CallOpParam callOpParam = new CallOpParam();
            callOpParam.setStatusCode(pjsip_status_code.PJSIP_SC_RINGING);
            call.answer(callOpParam);
            Logger.debug(LOG_TAG, "Sending 180 ringing");

            String displayName, remoteUri;
            try {
                CallerInfo contactInfo = new CallerInfo(call.getInfo());
                displayName = contactInfo.getDisplayName();
                remoteUri = contactInfo.getRemoteUri();
            } catch (Exception ex) {
                Logger.error(LOG_TAG, "Error while getting caller info", ex);
                throw ex;
            }

            // check for video in remote SDP
            CallInfo callInfo = call.getInfo();
            boolean isVideo = (callInfo.getRemOfferer() && callInfo.getRemVideoCount() > 0);

            service.getBroadcastEmitter().incomingCall(data.getIdUri(service.getApplicationContext()), prm.getCallId(),
                            displayName, remoteUri, isVideo);

        } catch (Exception ex) {
            Logger.error(LOG_TAG, "Error while getting caller info", ex);
        }*/
    }

    public boolean isActiveCallPresent() {
        return !activeCalls.isEmpty() || activeIncomingCall != null;
    }

    public boolean noActiveCallPresent() {
        return activeCalls.isEmpty() && activeIncomingCall == null;
    }

    /**
     * Get active incoming call {@link ICall}
     *
     * @return {@link ICall}, which can be cast to {@link IncomingCall} as
     * and when required
     */
    public ICall getActiveIncomingCall() {
        return activeIncomingCall;
    }

    public void setActiveIncomingCall(ICall activeIncomingCall) {
        this.activeIncomingCall = activeIncomingCall;
    }

    public SipCall getActiveCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Optional<Integer> peekCall = activeCalls.keySet().stream().findFirst();
            return peekCall.map(activeCalls::get).orElse(null);
        } else {
            Map.Entry<Integer, SipCall> firstEntry = activeCalls.entrySet().iterator().next();
            return firstEntry.getValue();
        }
    }
}
