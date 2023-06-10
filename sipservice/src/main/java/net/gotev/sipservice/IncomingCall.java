/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package net.gotev.sipservice;


import java.io.Serializable;

/**
 * Model class representing incoming call object
 */
public class IncomingCall implements Serializable, ICall {
    private String server;
    private String slot;
    private String linkedUUID;
    private String callerCname;
    private String callerName;
    private String imageUrl;
    private CallState state;
    private long time;
    private CallType callType;
    private boolean holdCall;
    private boolean isVideoCall;
    private String number;


    /**
     * This method is used to get callerName of incoming call
     *
     * @return callerName of incoming number
     */
    @Override
    public String getCallName() {
        return callerName;
    }


    /**
     * This method is used to get if the incoming call is hold or not
     *
     * @return holdCall true if hold else false
     */
    @Override
    public boolean isHoldCall() {
        return holdCall;
    }


    /**
     * This method is used to hold incoming call
     *
     * @param isHoldCall boolean hold or unhold
     */
    @Override
    public void setHoldCall(boolean isHoldCall) {
        this.holdCall = isHoldCall;
    }


    /**
     * This method is used to get callId present for incoming call
     *
     * @return callId exist or not for incoming call
     */
    @Override
    public boolean isCallIdPresent(int callId) {
        return false;
    }


    /**
     * This method is used to get calling state of incoming call
     *
     * @return callState {@link CallState}
     */
    @Override
    public CallState getState() {
        return state;
    }


    /**
     * This method is used to set different calling states for incoming call
     *
     * @param connected {@link CallState}
     */
    @Override
    public void setState(CallState connected) {
        this.state = connected;
    }


    /**
     * This method is used to set that incoming call is active or not
     *
     * @param active incoming call is active or not
     */
    @Override
    public void setActive(boolean active) {

    }


    /**
     * This method is used to get imageUrl of incoming call
     *
     * @return imageUrl of incoming number
     */
    @Override
    public String getImageUrl() {
        return imageUrl;
    }


    /**
     * This method is used to get linkedUUID of incoming call
     *
     * @return linkedUUID of incoming call
     */
    @Override
    public String getLinkedUUID() {
        return linkedUUID;
    }


    /**
     * This method is used to set linkedUUID of incoming call
     *
     * @param linkedUUID of incoming call
     */
    @Override
    public void setLinkedUUID(String linkedUUID) {
        this.linkedUUID = linkedUUID;
    }


    /**
     * This method is used to set timestamp of incoming call
     *
     * @param time timestamp of incoming call
     */
    @Override
    public void setTime(long time) {
        this.time = time;
    }


    /**
     * This method is used to get timestamp of incoming call
     *
     * @return timestamp of incoming call
     */
    @Override
    public long getTime() {
        return time;
    }


    /**
     * This method is used to set call type of incoming call
     *
     * @param callType {@link CallType}
     */
    @Override
    public void setCallType(CallType callType) {
        this.callType = callType;
    }


    /**
     * This method is used to get  call type of incoming call
     *
     * @return callType of incoming call {@link CallType}
     */
    @Override
    public CallType getCallType() {
        return callType;
    }


    /**
     * This method is used to set server of incoming call
     *
     * @param server of incoming call
     */
    public void setServer(String server) {
        this.server = server;
    }


    /**
     * This method is used to set slot of incoming call
     *
     * @param slot of incoming call
     */
    public void setSlot(String slot) {
        this.slot = slot;
    }


    /**
     * This method is used to get slot of incoming call
     *
     * @return slot of incoming call
     */
    public String getSlot() {
        return slot;
    }


    /**
     * This method is used to set callerCname of incoming call
     *
     * @param callerCname of incoming call
     */
    @Override
    public void setCallerNumber(String callerCname) {
        this.callerCname = callerCname;
    }


    /**
     * This method is used to set callerName of incoming call
     *
     * @param callerName of incoming call
     */
    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }


    /**
     * This method is used to set image url of incoming call
     *
     * @param imageUrl of incoming call
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    /**
     * This method is used to get server of incoming call
     *
     * @return sever
     */
    public String getServer() {
        return server;
    }


    /**
     * This method is used to get callerCname of incoming call
     *
     * @return callerCname of incoming call
     */
    @Override
    public String getCallerNumber() {
        return callerCname;
    }


    /**
     * This method is used to get caller name of incoming call
     *
     * @return callername of incoming call
     */
    public String getCallerName() {
        return callerName;
    }


    /**
     * This method is used to get incoming call is video call
     *
     * @return incoming call is video call
     */
    public boolean isVideoCall() {
        return isVideoCall;
    }


    /**
     * This method is used to set the incoming call as video call
     *
     * @param videoCall boolean to set incoming call as video call
     */
    public void setVideoCall(boolean videoCall) {
        isVideoCall = videoCall;
    }


    /**
     * This method is used to get number of incoming call
     *
     * @return number of incoming call
     */
    public String getNumber() {
        return number;
    }


    /**
     * This method is used to set number of incoming call
     *
     * @param number incoming call
     */
    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public boolean isCallOnMute() {
        return false;
    }
}
