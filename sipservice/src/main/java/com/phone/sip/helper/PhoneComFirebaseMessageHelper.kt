/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package com.phone.sip.helper

import android.content.Context
import com.google.gson.Gson
import com.phone.sip.Logger
import com.phone.sip.PhoneComServiceCommand
import com.phone.sip.constants.SipServiceConstants
import com.phone.sip.model.FCMResponse
import org.json.JSONException
import org.json.JSONObject


object PhoneComFirebaseMessageHelper {

    private val TAG = PhoneComFirebaseMessageHelper.javaClass.simpleName
    private const val APNS_VOIP = "APNS_VOIP"
    private const val APNS_VOIP_SANDBOX = "APNS_VOIP_SANDBOX"

    /**
     * Validates provided firebase message data and return true if messages are related to Phone.com
     * call notification. Validate message before process.
     */
    fun validate(messageData: String?): Boolean {
        Logger.debug(TAG, "alpha17 debug -> validate() -> $messageData")
        messageData?.let {
            val json: JSONObject
            var processedData: String? = messageData
            try {
                json = JSONObject(messageData)
                if (json.has(APNS_VOIP)) {
                    processedData = json.getString(APNS_VOIP)
                }
                if (json.has(APNS_VOIP_SANDBOX)) {
                    processedData = json.getString(APNS_VOIP_SANDBOX)
                }

                val fcmResponse = Gson().fromJson(processedData, FCMResponse::class.java)
                return fcmResponse.notifyType == SipServiceConstants.PDC_CALL_NOTIFICATION

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return false
    }


    /**
     * Process push message data and provides callback for the action Incoming or Missed call event
     * Also refer @see BroadcastEventReceiver#onIncomingCall()
     *
     * @param context Application Context
     * @param messageData Remote message data received as Push Notification from server
     */
    fun processMessageData(context: Context, messageData: String?) {
        if (validate(messageData)) {
            val data = Gson().fromJson(messageData, FCMResponse::class.java)
            Logger.debug(TAG, "alpha17 debug -> processMessageData() -> $data")
            PhoneComServiceCommand.handleIncomingCallPushNotification(
                context,
                data.status,
                data.from,
                data.server,
                data.slot,
                data.linkedUUID,
                data.callerName
            )

        } else {
            throw IllegalArgumentException("Invalid data received. Please refer document for valid push message format.")
        }
    }
}
