package com.phone.sip

import android.content.Context

class PhoneComLogger(
    var context: Context? = null,
    var enableSipConsoleLogging: Boolean,
    var enableSipFileLogging: Boolean,
    var logFilePath: String
) {

    private constructor(builder: Builder) : this(
        builder.context,
        builder.enableSipConsoleLogging,
        builder.enableSipFileLogging,
        builder.logFilePath
    )

    class Builder {
        var context: Context? = null
            private set
        var enableSipConsoleLogging: Boolean = false
            private set
        var enableSipFileLogging: Boolean = false
            private set
        var logFilePath: String = ""
            private set

        fun setContext(context: Context) = apply { this.context = context }

        fun setSipFileLoggingEnabled(enableSipFileLogging: Boolean, logFilePath: String) = apply {
            this.enableSipFileLogging = enableSipFileLogging
            this.logFilePath = logFilePath
        }

        fun setSipConsoleLoggingEnabled(enableSipConsoleLogging: Boolean) =
            apply { this.enableSipConsoleLogging = enableSipConsoleLogging }

        fun build(context: Context): PhoneComLogger {
            this.context = context

            PhoneComServiceCommand.setSipFileLoggingEnabled(
                enableSipFileLogging, logFilePath, context
            )

            PhoneComServiceCommand.setSipConsoleLoggingEnabled(
                enableSipConsoleLogging, context
            )

            return PhoneComLogger(this)
        }
    }
}