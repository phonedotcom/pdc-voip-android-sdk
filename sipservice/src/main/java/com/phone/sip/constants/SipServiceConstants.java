package com.phone.sip.constants;

public interface SipServiceConstants {

    /*
     * Intent Actions for Sip Service
     */
    String ACTION_RESTART_SIP_STACK = "restartSipStack";
    String ACTION_SET_ACCOUNT = "setAccount";
    String ACTION_REMOVE_ACCOUNT = "removeAccount";
    String ACTION_MAKE_CALL = "makeCall";
    String ACTION_HANG_UP_CALL = "hangUpCall";
    String ACTION_HANG_UP_CALLS = "hangUpCalls";
    String ACTION_HOLD_CALLS = "holdCalls";
    String ACTION_GET_CALL_STATUS = "getCallStatus";
    String ACTION_SEND_DTMF = "sendDtmf";
    String ACTION_ACCEPT_INCOMING_CALL = "acceptIncomingCall";
    String ACTION_DECLINE_INCOMING_CALL = "declineIncomingCall";
    String ACTION_SET_HOLD = "callSetHold";
    String ACTION_SET_MUTE = "callSetMute";
    String ACTION_TOGGLE_HOLD = "callToggleHold";
    String ACTION_TOGGLE_MUTE = "callToggleMute";
    String ACTION_TRANSFER_CALL = "callTransfer";
    String ACTION_ATTENDED_TRANSFER_CALL = "callAttendedTransfer";
    String ACTION_GET_CODEC_PRIORITIES = "codecPriorities";
    String ACTION_SET_CODEC_PRIORITIES = "setCodecPriorities";
    String ACTION_GET_REGISTRATION_STATUS = "getRegistrationStatus";
    String ACTION_REFRESH_REGISTRATION = "refreshRegistration";
    String ACTION_SET_DND = "setDND";
    String ACTION_SET_INCOMING_VIDEO = "setIncomingVideo";
    String ACTION_SET_SELF_VIDEO_ORIENTATION = "setSelfVideoOrientation";
    String ACTION_SET_VIDEO_MUTE = "setVideoMute";
    String ACTION_START_VIDEO_PREVIEW = "startVideoPreview";
    String ACTION_STOP_VIDEO_PREVIEW = "stopVideoPreview";
    String ACTION_SWITCH_VIDEO_CAPTURE_DEVICE = "switchVideoCaptureDevice";
    String ACTION_MAKE_DIRECT_CALL = "makeDirectCall";
    String ACTION_RECONNECT_CALL = "reconnectCall";
    String ACTION_MAKE_SILENT_CALL = "makeSilentCall";
    String ACTION_REJECT_CALL_USER_BUSY = "rejectCallUserBusy";
    String ACTION_UNREGISTER_PUSH_LOGOUT = "unregisterPushLogout";
    /*
     * Generic Parameters
     */
    String PARAM_ACCOUNT_DATA = "accountData";
    String PARAM_ACCOUNT_ID = "accountID";
    String PARAM_NUMBER = "number";
    String PARAM_CALL_ID = "callId";
    String PARAM_CALL_ID_DEST = "callIdDest";
    String PARAM_DTMF = "dtmf";
    String PARAM_HOLD = "hold";
    String PARAM_MUTE = "mute";
    String PARAM_CODEC_PRIORITIES = "codecPriorities";
    String PARAM_REG_EXP_TIMEOUT = "regExpTimeout";
    String PARAM_REG_CONTACT_PARAMS = "regContactParams";
    String PARAM_DND = "dnd";
    String PARAM_IS_VIDEO = "isVideo";
    String PARAM_IS_VIDEO_CONF = "isVideoConference";
    String PARAM_SURFACE = "surface";
    String PARAM_ORIENTATION = "orientation";
    String PARAM_GUEST_NAME = "guestName";
    String PARAM_DIRECT_CALL_URI = "sipUri";
    String PARAM_DIRECT_CALL_SIP_SERVER = "sipServer";
    String PARAM_DIRECT_CALL_TRANSPORT = "directTransport";
    String PARAM_IS_TRANSFER = "isTransfer";

    /**
     * Specific Parameters passed in the broadcast intents.
     */
    String PARAM_REGISTRATION_CODE = "registrationCode";
    String PARAM_REMOTE_URI = "remoteUri";
    String PARAM_DISPLAY_NAME = "displayName";
    String PARAM_CALL_EVENT = "callEvent";
    String PARAM_CALL_STATUS = "callStatus";
    String PARAM_CONNECT_TIMESTAMP = "connectTimestamp";
    String PARAM_STACK_STARTED = "stackStarted";
    String PARAM_CODEC_PRIORITIES_LIST = "codecPrioritiesList";
    String PARAM_MEDIA_STATE_KEY = "mediaStateKey";
    String PARAM_MEDIA_STATE_VALUE = "mediaStateValue";
    String PARAM_VIDEO_MUTE = "videoMute";
    String PARAM_SUCCESS = "success";
    String PARAM_INCOMING_VIDEO_WIDTH = "incomingVideoWidth";
    String PARAM_INCOMING_VIDEO_HEIGHT = "incomingVideoHeight";
    String PARAM_CALL_RECONNECTION_STATE = "callReconnectionState";
    String PARAM_SILENT_CALL_STATUS = "silentCallStatus";

    String PARAM_INCOMING_FROM = "incomingFrom";
    String PARAM_INCOMING_SERVER = "incomingServer";
    String PARAM_INCOMING_SLOT = "incomingSlot";
    String PARAM_INCOMING_LINKED_UUID = "incomingLinkedUuid";
    String PARAM_INCOMING_STATUS = "incomingStatus";
    String PARAM_ANY_ACTIVE_CALL = "isAnyActiveCall";
    String PARAM_CALL_MEDIA_EVENT_TYPE = "callMediaEventType";
    String PARAM_INCOMING_CALL_DATA = "incomingCallData";
    String PARAM_MISSED_CALL_DATA = "missedCallData";
    String PARAM_INITIALIZE_STATUS = "initializeStatus";

    /**
     * Specific Parameters passed in the broadcast intents for call stats.
     */
    String PARAM_CALL_STATS_DURATION = "callStatsDuration";
    String PARAM_CALL_STATS_AUDIO_CODEC = "callStatsAudioCodec";
    String PARAM_CALL_STATS_CALL_STATUS = "callStatsCallStatus";
    String PARAM_CALL_STATS_RX_STREAM = "callStatsRxStream";
    String PARAM_CALL_STATS_TX_STREAM = "callStatsTxStream";

    /**
     * Video Configuration Params
     */

    int FRONT_CAMERA_CAPTURE_DEVICE = 1;    // Front Camera idx
    int BACK_CAMERA_CAPTURE_DEVICE = 2;     // Back Camera idx
    int DEFAULT_RENDER_DEVICE = 0;          // OpenGL Render
    String OPENH264_CODEC_ID = "H264/97";
    int H264_DEF_WIDTH = 640;
    int H264_DEF_HEIGHT = 360;
    String ANDROID_H264_CODEC_ID = "H264/99";
    String ANDROID_VP8_CODEC_ID = "VP8/103";
    String ANDROID_VP9_CODEC_ID = "VP9/106";

    /**
     * Janus Bridge call specific parameters.
     */
    String PROFILE_LEVEL_ID_HEADER = "profile-level-id";
    String PROFILE_LEVEL_ID_LOCAL = "42e01e";
    String PROFILE_LEVEL_ID_JANUS_BRIDGE = "42e01f";

    /**
     * Generic Constants
     */
    int DELAYED_JOB_DEFAULT_DELAY = 5000;

    int DELAY_STOP_SERVICE = 200;

    /**
     * SIP DEFAULT PORTS
     */
    int DEFAULT_SIP_PORT = 5060;

    /**
     * PJSIP TLS VERIFY PEER ERROR
     */
    int PJSIP_TLS_ECERTVERIF = 171173;

    /**
     * This should be changed on the app side
     * to reflect app version/name/... or whatever might be useful for debugging
     */
    String AGENT_NAME = "MobileOffice";

    String PARAM_USERNAME = "USERNAME";

    String ACTION_INCOMING_CALL_DISCONNECTED = "incomingCallDisconnected";
    String ACTION_INCOMING_CALL_NOTIFICATION = "incomingCallNotification";

    String PARAM_ERROR_MESSAGE = "errorMessage";
    String PARAM_CALLER_NAME = "caller_name";
    String PARAM_TIME = "time";
    String PARAM_SECONDS = "seconds";
    String PARAM_CALL_TYPE = "call_type";
    String PARAM_IS_INCOMING_CALL = "isIncomingCall";
    String PARAM_PHONE_NUMBER = "phone_number";
    String PARAM_ERROR_CODE_WHILE_REJECTING_INCOMING_CALL = "errorCodeWhileRejectingIncomingCall";
    int PDC_CALL_NOTIFICATION = 4;

    /*
     * Generic error messages
     */
    String ERR_SIP_ACCOUNT_NULL = "Sip account in not found.";
    String ERR_SIP_CALL_NULL = "No Sip call found with given call id.";
    String PARAM_APP_NAME = "Phone.com";
    String ERROR_INITIALIZE_MISSING_PARAMS = "Missing parameters required for library initialization. Please refer this document, https://github.com/phonedotcom/pdc-voip-android-sdk#4-phonecom-service-configuration-and-initialization";
    String ERR_MICROPHONE_PERMISSION_NOT_ALLOWED = "Microphone permission is not allowed by user. Please ask user to allow Microphone Permission in order to use the VOIP calling";

    String ERR_WRITE_STORAGE_PERMISSION_NOT_ALLOWED = "There is an error in creating or writing in the file, Please verify if storage permission is allowed or not by user to write file.";
    String ERR_LOG_FILE_NOT_FOUND = "File not found to store logs, Please provide valid file path or see instruction on how to enable the SIP file logging from document.";
    String ERR_LOG_FILE_FOUND_LOG_DISABLED = "Log file path is provided but File logging is disabled, Hence logs will not be written in the file.";

    long DELAY_ACCEPT_INCOMING_CALL = 1000L;
}
