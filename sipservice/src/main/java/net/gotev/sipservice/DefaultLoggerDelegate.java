package net.gotev.sipservice;

import android.util.Log;

/**
 * Default logger delegate implementation which logs in LogCat with {@link Log}.
 *
 * @author gotev (Aleksandar Gotev)
 */
public class DefaultLoggerDelegate implements Logger.LoggerDelegate {

    public static final String PREFIX = "Phone.com | ";

    @Override
    public void error(String tag, String message) {
        Log.e(PREFIX + tag, message);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        Log.e(PREFIX + tag, message, exception);
    }

    @Override
    public void debug(String tag, String message) {
        Log.d(PREFIX + tag, message);
    }

    @Override
    public void info(String tag, String message) {
        Log.i(PREFIX + tag, message);
    }
}
