package net.gotev.sipservice.models

data class ConfigureSip(
    var sipUsername: String,
    var sipPassword: String,
    var domainName: String,
    var port: Int,
    var securePort: Int,
    var secureProtocolName: String,
    var protocolName: String
)