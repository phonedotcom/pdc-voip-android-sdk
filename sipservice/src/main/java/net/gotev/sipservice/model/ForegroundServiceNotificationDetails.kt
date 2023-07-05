package net.gotev.sipservice.model

import android.content.Context

data class ForegroundServiceNotificationDetails(
    val context: Context,
    val appName: String,
    val notificationMessage: String,
    val notificationIcon: Int
)