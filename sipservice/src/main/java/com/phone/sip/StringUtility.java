package com.phone.sip;

public class StringUtility {
    public static boolean validateString(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
