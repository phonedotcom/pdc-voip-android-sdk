package com.phone.sip.constants

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class InitializeStatus : Parcelable {
    @Parcelize
    class Success(var username: String) : InitializeStatus()

    @Parcelize
    class Failure(var errorMessage: String) : InitializeStatus()
}