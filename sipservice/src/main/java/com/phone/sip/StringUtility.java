package com.phone.sip;

public class StringUtility {
    public static boolean validate(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
