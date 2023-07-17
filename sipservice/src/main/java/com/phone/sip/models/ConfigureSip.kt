package com.phone.sip.models

import org.jetbrains.annotations.NotNull

data class ConfigureSip(
    @NotNull
    var sipUsername: String,
    @NotNull
    var sipPassword: String,
    @NotNull
    var domainName: String,
    @NotNull
    var port: Int,
    @NotNull
    var securePort: Int,
    @NotNull
    var secureProtocolName: String,
    @NotNull
    var protocolName: String
)