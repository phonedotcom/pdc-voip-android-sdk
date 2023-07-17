/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package com.phone.sip;

/* X-Disconnect: true - as before, results in a 603 response back to the caller
   X-Disconnect: USER_BUSY - results in a 486 Busy Here back to the caller
   X-Disconnect: SUBSCRIBER_ABSENT - results in a 480 back to the caller
   X-Disconnect: ORIGINATOR_CANCEL - results in a 487 back to the caller
   X-Disconnect: CALL_REJECTED - results in a 603 back to the caller*/
public enum ErrorCodes {
    USER_BUSY, SUBSCRIBER_ABSENT, ORIGINATOR_CANCEL, CALL_REJECTED, TRUE
}
