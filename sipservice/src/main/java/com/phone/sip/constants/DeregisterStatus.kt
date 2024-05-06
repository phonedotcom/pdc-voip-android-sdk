package com.phone.sip.constants

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class DeregisterStatus : Parcelable {
    @Parcelize
    class Success(var message: String) : DeregisterStatus()

    @Parcelize
    class Failure(var errorMessage: String) : DeregisterStatus()

    @Parcelize
    class InProgress(var message: String): DeregisterStatus()

}