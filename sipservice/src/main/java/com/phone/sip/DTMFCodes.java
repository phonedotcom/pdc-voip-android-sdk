package com.phone.sip;

import androidx.annotation.NonNull;

public enum DTMFCodes {
    NINE("9");

    private final String code;

    DTMFCodes(final String code) {
        this.code = code;
    }

    @NonNull
    @Override
    public String toString() {
        return code;
    }
}
