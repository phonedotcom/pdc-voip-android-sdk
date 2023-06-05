package net.gotev.sipservice;

public class StringUtility {
    public static boolean validateString(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
