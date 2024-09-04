/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package com.phone.sip;

import java.io.Serializable;


public interface ICall extends Serializable{

    /**
     * Method for returning the CallerName, uses {@link #getNumber()}, it performs operation on
     * {@link #getNumber()} to return name based on formatting and custom logic in implementation
     * class.
     *
     * @return the callerName
     */
    String getCallerName();

    /**
     * Method for returning the CallerName
     * @see #getCallerName()
     * @return the caller name
     */
    String getNumber();

    /**
     * Method for setting the caller name
     * @param {@link String} callerNumber
     */
    void setNumber(String callerNumber);

    /**
     * Method for checking the hold unhold status of current call
     *
     * @return boolean indicating whether the current call is on hold
     * if true the call is on hold
     * else not
     */
    boolean isHoldCall();

    /**
     * Method for setting the hold/ unhold status if current call
     * @param isHoldCall boolean true-> call is on hold
     *                   false -> call in not on hold
     */
    void setHoldCall(boolean isHoldCall);

    /**
     * Method for checking whether the current call is same as other one by comparing their call id
     *
     * @param callId callID to match
     * @return boolean true if both the calls match else false
     */
    boolean isCallIdPresent(int callId );

    /**
     * Method to get the state of call
     * @see CallState
     *
     * @return the current state of call
     */
    CallState getState();

    /**
     * Method for setting the state of call
     * @see CallState
     *
     * @param connected indicates the state of call
     */
    void setState(CallState connected);

    /**
     * Method to set the active status of call
     * @param active if true call is active else not
     */
    void setActive(boolean active);


    String getImageUrl();

    /**
     * @return {@link String} LinkedUUID of Call
     */
    String getLinkedUUID();

    void setLinkedUUID(String linkedUUID);

    /**
     * Method for setting the time of call
     *
     * @param time long time of call
     */
    void setTime(long time);

    /**
     * Method for getting the time of call
     *
     * @return the time of call in long
     */
    long getTime();

    /**
     * Method to set the type of call
     *
     * @param callType  the type of call
     * @see CallType
     */
    void setCallType(CallType callType);

    /**
     * Method to retrieve the type of call
     *
     * @return the type of call
     * @see CallType
     */
    CallType getCallType();

    int getId();

    boolean isCallOnMute();
}
