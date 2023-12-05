package com.phone.sip;

import static com.phone.sip.constants.SipServiceConstants.ERR_LOG_FILE_NOT_FOUND;
import static com.phone.sip.constants.SipServiceConstants.ERR_WRITE_STORAGE_PERMISSION_NOT_ALLOWED;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import org.pjsip.pjsua2.LogEntry;
import org.pjsip.pjsua2.LogWriter;
import org.pjsip.pjsua2.pj_log_decoration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * connect
 * <p>
 * Created by Vincenzo Esposito on 15/10/21.
 * Copyright © 2021 VoiSmart S.r.l. All rights reserved.
 */
class SipLogger extends LogWriter {

    private final Context context;

    public SipLogger(Context context) {
        this.context = context;
    }

    public void write(LogEntry entry) {
        Logger.debug("SIP -> " + entry.getThreadName(), entry.getMsg());

        if (SharedPreferencesHelper.getInstance(context).getBooleanPreference(SharedPreferenceConstant.ENABLE_SIP_FILE_LOGS, false)) {
            String logFileName = SharedPreferencesHelper.getInstance(context).getStringSharedPreference(SharedPreferenceConstant.LOGS_FILE_NAME);
            File logFile = new File(logFileName);

            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.R &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Logger.error("SIP -> " + entry.getThreadName(), ERR_WRITE_STORAGE_PERMISSION_NOT_ALLOWED);
            }

            if (!logFile.exists()) {
                Logger.error("SIP -> " + entry.getThreadName(), ERR_LOG_FILE_NOT_FOUND);
                return;
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(logFileName, true)) {
                OutputStreamWriter outputStream = new OutputStreamWriter(fileOutputStream);
                outputStream.append(entry.getMsg());
                outputStream.append("\n\n\n");
                outputStream.flush();
                outputStream.close();
                fileOutputStream.flush();
            } catch (Exception e) {
                Logger.error("SIP -> " + entry.getThreadName(), ERR_LOG_FILE_NOT_FOUND);
            }
        }
    }

    /**
     * Change decor flags as needed
     * @return decor flags
     */
    public long getDecor() {
        return pj_log_decoration.PJ_LOG_HAS_CR
                | pj_log_decoration.PJ_LOG_HAS_NEWLINE
                | pj_log_decoration.PJ_LOG_HAS_COLOR
                | pj_log_decoration.PJ_LOG_HAS_INDENT
                | pj_log_decoration.PJ_LOG_HAS_LEVEL_TEXT
                | pj_log_decoration.PJ_LOG_HAS_TIME
                | pj_log_decoration.PJ_LOG_HAS_SENDER
                | pj_log_decoration.PJ_LOG_HAS_SPACE;
    }
}
