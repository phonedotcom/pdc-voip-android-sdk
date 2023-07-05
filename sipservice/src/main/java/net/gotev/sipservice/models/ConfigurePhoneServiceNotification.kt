package net.gotev.sipservice.models

data class ConfigurePhoneServiceNotification(
    var appName: String,
    var notificationMessage: String,
    var notificationIcon: Int
)