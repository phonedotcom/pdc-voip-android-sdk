package net.gotev.sipservice

import android.content.Context

class SipInitialization(
    var context: Context?,
    var sipUsername: String,
    var sipPassword: String,
    var domainName: String,
    var port: Int,
    var securePort: Int,
    var secureProtocolName: String,
    var protocolName: String
) {

    private constructor(builder: Builder) : this(
        builder.context,
        builder.sipUsername,
        builder.sipPassword,
        builder.domainName,
        builder.port,
        builder.securePort,
        builder.secureProtocolName,
        builder.protocolName
    )

    class Builder {
        var context: Context? = null
            private set
        var sipUsername: String = ""
            private set
        var sipPassword: String = ""
            private set
        var domainName: String = ""
            private set
        var port: Int = 0
            private set
        var securePort: Int = 0
            private set
        var secureProtocolName: String = ""
            private set
        var protocolName: String = ""
            private set

        fun build() = SipInitialization(this)

        fun context(context: Context) = apply { this.context = context }

        fun sipUsername(sipUsername: String) = apply { this.sipUsername = sipUsername }

        fun sipPassword(sipPassword: String) = apply { this.sipPassword = sipPassword }

        fun domainName(domainName: String) = apply { this.domainName = domainName }

        fun port(port: Int) = apply { this.port = port }

        fun securePort(securePort: Int) = apply { this.securePort = securePort }

        fun secureProtocolName(secureProtocolName: String) =
            apply { this.secureProtocolName = secureProtocolName }

        fun protocolName(protocolName: String) =
            apply { this.protocolName = protocolName }
    }
}