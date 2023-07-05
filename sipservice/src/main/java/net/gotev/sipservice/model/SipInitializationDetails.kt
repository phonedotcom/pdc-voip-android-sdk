package net.gotev.sipservice.model

import android.content.Context

data class SipInitializationDetails(
    val context: Context,
    val sipUsername: String,
    val sipPassword: String,
    val domainName: String,
    val port: Int,
    val securePort: Int,
    val secureProtocolName: String,
    val protocolName: String
)
