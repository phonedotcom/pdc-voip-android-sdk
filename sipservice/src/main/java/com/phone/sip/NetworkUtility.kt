package com.phone.sip

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtility {

    fun isConnectedToInternet(context: Context): Boolean {
        return isWifiConnected(context) || isCellularNetworkConnected(context)
    }

    private fun isWifiConnected(context: Context): Boolean {
        val info =
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        return info != null && info.isConnected && info.type == ConnectivityManager.TYPE_WIFI
    }

    private fun isCellularNetworkConnected(context: Context): Boolean {
        val info =
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        return info != null && info.isConnected && info.type == ConnectivityManager.TYPE_MOBILE
    }
}