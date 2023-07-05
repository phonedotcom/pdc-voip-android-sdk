package net.gotev.sipservice

import android.content.Context
import net.gotev.sipservice.model.SipInitializationDetails

class SipInitializationBuilder {

    private var context: Context? = null
    private var sipUsername: String = ""
    private var sipPassword: String = ""
    private var domainName: String = ""
    private var port: Int = 0
    private var securePort: Int = 0
    private var secureProtocolName: String = ""
    private var protocolName: String = ""

    fun build(): SipInitializationDetails {
        return SipInitializationDetails(
            context!!,
            sipUsername,
            sipPassword,
            domainName,
            port,
            securePort,
            secureProtocolName,
            protocolName
        )
    }

    fun setContext(context: Context): SipInitializationBuilder {
        this.context = context
        return this
    }

    fun setSipUsername(sipUsername: String): SipInitializationBuilder {
        this.sipUsername = sipUsername
        return this
    }

    fun setSipPassword(sipPassword: String): SipInitializationBuilder {
        this.sipPassword = sipPassword
        return this
    }

    fun setDomainName(domainName: String): SipInitializationBuilder {
        this.domainName = domainName
        return this
    }

    fun setPort(port: Int): SipInitializationBuilder {
        this.port = port
        return this
    }

    fun setSecurePort(securePort: Int): SipInitializationBuilder {
        this.securePort = securePort
        return this
    }

    fun setSecureProtocolName(secureProtocolName: String): SipInitializationBuilder {
        this.secureProtocolName = secureProtocolName
        return this
    }

    fun setProtocolName(protocolName: String): SipInitializationBuilder {
        this.protocolName = protocolName
        return this
    }
}