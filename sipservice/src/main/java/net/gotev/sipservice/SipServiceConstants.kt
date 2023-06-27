package net.gotev.sipservice

object SipServiceConstants {
    const val NAMESPACE = "com.phone"

    /*
 * Intent Actions for Sip Service
 */
    const val ACTION_RESTART_SIP_STACK = "restartSipStack"
    const val ACTION_SET_ACCOUNT = "setAccount"
    const val ACTION_REMOVE_ACCOUNT = "removeAccount"
    const val ACTION_MAKE_CALL = "makeCall"
    const val ACTION_HANG_UP_CALL = "hangUpCall"
    const val ACTION_HANG_UP_CALLS = "hangUpCalls"
    const val ACTION_HOLD_CALLS = "holdCalls"
    const val ACTION_GET_CALL_STATUS = "getCallStatus"
    const val ACTION_SEND_DTMF = "sendDtmf"
    const val ACTION_ACCEPT_INCOMING_CALL = "acceptIncomingCall"
    const val ACTION_DECLINE_INCOMING_CALL = "declineIncomingCall"
    const val ACTION_SET_HOLD = "callSetHold"
    const val ACTION_SET_MUTE = "callSetMute"
    const val ACTION_TOGGLE_HOLD = "callToggleHold"
    const val ACTION_TOGGLE_MUTE = "callToggleMute"
    const val ACTION_TRANSFER_CALL = "callTransfer"
    const val ACTION_ATTENDED_TRANSFER_CALL = "callAttendedTransfer"
    const val ACTION_GET_CODEC_PRIORITIES = "codecPriorities"
    const val ACTION_SET_CODEC_PRIORITIES = "setCodecPriorities"
    const val ACTION_GET_REGISTRATION_STATUS = "getRegistrationStatus"
    const val ACTION_REFRESH_REGISTRATION = "refreshRegistration"
    const val ACTION_SET_DND = "setDND"
    const val ACTION_SET_INCOMING_VIDEO = "setIncomingVideo"
    const val ACTION_SET_SELF_VIDEO_ORIENTATION = "setSelfVideoOrientation"
    const val ACTION_SET_VIDEO_MUTE = "setVideoMute"
    const val ACTION_START_VIDEO_PREVIEW = "startVideoPreview"
    const val ACTION_STOP_VIDEO_PREVIEW = "stopVideoPreview"
    const val ACTION_SWITCH_VIDEO_CAPTURE_DEVICE = "switchVideoCaptureDevice"
    const val ACTION_MAKE_DIRECT_CALL = "makeDirectCall"
    const val ACTION_RECONNECT_CALL = "reconnectCall"
    const val ACTION_MAKE_SILENT_CALL = "makeSilentCall"
    const val ACTION_REJECT_CALL_USER_BUSY = "rejectCallUserBusy"
    const val ACTION_UNREGISTER_PUSH_LOGOUT = "unregisterPushLogout"

    /*
 * Generic Parameters
 */
    const val PARAM_ACCOUNT_DATA = "accountData"
    const val PARAM_ACCOUNT_ID = "accountID"
    const val PARAM_NUMBER = "number"
    const val PARAM_CALL_ID = "callId"
    const val PARAM_CALL_ID_DEST = "callIdDest"
    const val PARAM_DTMF = "dtmf"
    const val PARAM_HOLD = "hold"
    const val PARAM_MUTE = "mute"
    const val PARAM_CODEC_PRIORITIES = "codecPriorities"
    const val PARAM_REG_EXP_TIMEOUT = "regExpTimeout"
    const val PARAM_REG_CONTACT_PARAMS = "regContactParams"
    const val PARAM_DND = "dnd"
    const val PARAM_IS_VIDEO = "isVideo"
    const val PARAM_IS_VIDEO_CONF = "isVideoConference"
    const val PARAM_SURFACE = "surface"
    const val PARAM_ORIENTATION = "orientation"
    const val PARAM_GUEST_NAME = "guestName"
    const val PARAM_DIRECT_CALL_URI = "sipUri"
    const val PARAM_DIRECT_CALL_SIP_SERVER = "sipServer"
    const val PARAM_DIRECT_CALL_TRANSPORT = "directTransport"
    const val PARAM_IS_TRANSFER = "isTransfer"

    /**
     * Specific Parameters passed in the broadcast intents.
     */
    const val PARAM_REGISTRATION_CODE = "registrationCode"
    const val PARAM_REMOTE_URI = "remoteUri"
    const val PARAM_DISPLAY_NAME = "displayName"
    const val PARAM_CALL_STATE = "callState"
    const val PARAM_CALL_STATUS = "callStatus"
    const val PARAM_CONNECT_TIMESTAMP = "connectTimestamp"
    const val PARAM_STACK_STARTED = "stackStarted"
    const val PARAM_CODEC_PRIORITIES_LIST = "codecPrioritiesList"
    const val PARAM_MEDIA_STATE_KEY = "mediaStateKey"
    const val PARAM_MEDIA_STATE_VALUE = "mediaStateValue"
    const val PARAM_VIDEO_MUTE = "videoMute"
    const val PARAM_SUCCESS = "success"
    const val PARAM_INCOMING_VIDEO_WIDTH = "incomingVideoWidth"
    const val PARAM_INCOMING_VIDEO_HEIGHT = "incomingVideoHeight"
    const val PARAM_CALL_RECONNECTION_STATE = "callReconnectionState"
    const val PARAM_SILENT_CALL_STATUS = "silentCallStatus"
    const val PARAM_INCOMING_FROM = "incomingFrom"
    const val PARAM_INCOMING_SERVER = "incomingServer"
    const val PARAM_INCOMING_SLOT = "incomingSlot"
    const val PARAM_INCOMING_LINKED_UUID = "incomingLinkedUuid"
    const val PARAM_INCOMING_STATUS = "incomingStatus"
    const val PARAM_NO_ACTIVE_CALL = "isActiveCallPresent"
    const val PARAM_IS_CALL = "isCall"
    const val PARAM_CALL_MEDIA_EVENT_TYPE = "callMediaEventType"

    /**
     * Specific Parameters passed in the broadcast intents for call stats.
     */
    const val PARAM_CALL_STATS_DURATION = "callStatsDuration"
    const val PARAM_CALL_STATS_AUDIO_CODEC = "callStatsAudioCodec"
    const val PARAM_CALL_STATS_CALL_STATUS = "callStatsCallStatus"
    const val PARAM_CALL_STATS_RX_STREAM = "callStatsRxStream"
    const val PARAM_CALL_STATS_TX_STREAM = "callStatsTxStream"

    /**
     * Video Configuration Params
     */
    const val FRONT_CAMERA_CAPTURE_DEVICE = 1 // Front Camera idx
    const val BACK_CAMERA_CAPTURE_DEVICE = 2 // Back Camera idx
    const val DEFAULT_RENDER_DEVICE = 0 // OpenGL Render
    const val OPENH264_CODEC_ID = "H264/97"
    const val H264_DEF_WIDTH = 640
    const val H264_DEF_HEIGHT = 360
    const val ANDROID_H264_CODEC_ID = "H264/99"
    const val ANDROID_VP8_CODEC_ID = "VP8/103"
    const val ANDROID_VP9_CODEC_ID = "VP9/106"

    /**
     * Janus Bridge call specific parameters.
     */
    const val PROFILE_LEVEL_ID_HEADER = "profile-level-id"
    const val PROFILE_LEVEL_ID_LOCAL = "42e01e"
    const val PROFILE_LEVEL_ID_JANUS_BRIDGE = "42e01f"

    /**
     * Generic Constants
     */
    const val DELAYED_JOB_DEFAULT_DELAY = 5000
    const val DELAY_STOP_SERVICE = 200

    /**
     * SIP DEFAULT PORTS
     */
    const val DEFAULT_SIP_PORT = 5060

    /**
     * PJSIP TLS VERIFY PEER ERROR
     */
    const val PJSIP_TLS_ECERTVERIF = 171173

    /**
     * This should be changed on the app side
     * to reflect app version/name/... or whatever might be useful for debugging
     */
    const val AGENT_NAME = "MobileOffice"
    const val PARAM_USERNAME = "USERNAME"
    const val GENERIC_PDC_VOIP_NOTIFICATION_CHANNEL = "GENERIC_PDC_VOIP_NOTIFICATION_CHANNEL"
    const val ACTION_INCOMING_CALL_DISCONNECTED = "incomingCallDisconnected"
    const val ACTION_INCOMING_CALL_NOTIFICATION = "incomingCallNotification"
    const val SERVICE_NOTIFICATION_CHANNEL_ID = "serviceNotificationChannelId"
    const val INTENT_HANDLED = "intentHandled"
    const val SERVICE_FOREGROUND_NOTIFICATION_ID = 121
    const val MISS_CALL_NOTIFICATION_CHANNEL = "missCallNotificationChannelId"
    const val INCOMING_CALL_NOTIFICATION_CHANNEL_ID = "incomingCallNotificationChannelId"
    const val MISSED_NOTIFICATION_ID = 1674
    const val HANGUP_BROADCAST_ACTION_ID = 2
    const val ACCEPT_CALL_BROADCAST_ACTION_ID = 1
    const val PARAM_ERROR_MESSAGE = "errorMessage"
    const val PARAM_CALLER_NAME = "caller_name"
    const val PARAM_TIME = "time"
    const val PARAM_SECONDS = "seconds"
    const val PARAM_CALL_TYPE = "call_type"
    const val PARAM_IS_INCOMING_CALL = "isIncomingCall"
    const val PARAM_PHONE_NUMBER = "phone_number"
    const val PARAM_ERROR_CODE_WHILE_REJECTING_INCOMING_CALL = "errorCodeWhileRejectingIncomingCall"

    /**
     * Generic error messages
     */
    const val ERR_SIP_ACCOUNT_NULL = "Sip account in not found."
    const val ERR_SIP_CALL_NULL = "No Sip call found with given call id."
}