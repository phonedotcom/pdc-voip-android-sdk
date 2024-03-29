package com.phone.sip;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.voismart.crypto.Crypto;
import com.voismart.crypto.EncryptionHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * connect
 * <p>
 * Created by Vincenzo Esposito on 25/11/19.
 * Copyright © 2019 VoiSmart S.r.l. All rights reserved.
 */
@SuppressWarnings("unused")
class SharedPreferencesHelper {

    @SuppressWarnings("FieldCanBeLocal")
    private final String PREFS_FILE_NAME = "com.phone.preference";
    @SuppressWarnings("FieldCanBeLocal")
    private final String PREFS_ENCRYPTED_FILE_NAME = "sipservice_encrypted_prefs";

    private final String PREFS_KEY_ACCOUNTS = "accounts";
    private final String PREFS_KEY_CODEC_PRIORITIES = "codec_priorities";
    private final String PREFS_KEY_DND = "dnd_pref";
    private final String PREFS_KEY_ENCRYPTION_ENABLED = "encryption_enabled";
    private final String PREFS_KEY_KEYSTORE_ALIAS = "keystore_alias";
    private final String PREFS_KEY_OBFUSCATION_ENABLED = "obfuscation_enabled";
    private final String PREFS_KEY_VERIFY_SIP_SERVER_CERT = "sip_server_cert_verification_enabled";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences encryptedSharedPreferences;
    private final Gson gson;
    private EncryptionHelper encryptionHelper = null;

    private static SharedPreferencesHelper INSTANCE = null;
    private static final String TAG = "SharedPreferenceHelper";

    private SharedPreferencesHelper(Context context) {
        gson = new Gson();
        sharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        encryptedSharedPreferences = initializeEncryptedSharedPreferences(context);
        migrateFrom(sharedPreferences, context);
        setObfuscation(false);
    }

    List<SipAccountData> retrieveConfiguredAccounts() {
        String accounts = encryptedSharedPreferences.getString(PREFS_KEY_ACCOUNTS, "");
        return getAccounts(accounts);
    }

    void persistConfiguredAccounts(List<SipAccountData> configuredAccounts) {
        encryptedSharedPreferences
                .edit()
                .putString(PREFS_KEY_ACCOUNTS, gson.toJson(configuredAccounts))
                .apply();
    }

    ArrayList<CodecPriority> retrieveConfiguredCodecPriorities() {
        String codecPriorities = sharedPreferences.getString(PREFS_KEY_CODEC_PRIORITIES, "");
        if (codecPriorities.isEmpty()) return null;

        Type listType = new TypeToken<ArrayList<CodecPriority>>() {
        }.getType();
        return gson.fromJson(codecPriorities, listType);
    }

    void persistConfiguredCodecPriorities(ArrayList<CodecPriority> codecPriorities) {
        sharedPreferences.edit().putString(PREFS_KEY_CODEC_PRIORITIES, gson.toJson(codecPriorities)).apply();
    }

    boolean isDND() {
        return sharedPreferences.getBoolean(PREFS_KEY_DND, false);
    }

    void setDND(boolean dnd) {
        sharedPreferences.edit().putBoolean(PREFS_KEY_DND, dnd).apply();
    }

    void setEncryption(Context context, boolean enableEncryption, String alias) {
    }

    void setObfuscation(boolean obfuscate) {
        sharedPreferences.edit().putBoolean(PREFS_KEY_OBFUSCATION_ENABLED, obfuscate).apply();
    }

    /**
     * Whether the string obfuscation is enabled in logs
     *
     * @return what is set in {@link #setObfuscation(boolean)} or true in release builds and false in debug ones
     */
    boolean isObfuscationEnabled() {
        return sharedPreferences.getBoolean(PREFS_KEY_OBFUSCATION_ENABLED, !BuildConfig.DEBUG);
    }

    void setVerifySipServerCert(boolean verify) {
        sharedPreferences.edit().putBoolean(PREFS_KEY_VERIFY_SIP_SERVER_CERT, verify).apply();
    }

    boolean isVerifySipServerCert() {
        return sharedPreferences.getBoolean(PREFS_KEY_VERIFY_SIP_SERVER_CERT, false);
    }

    /**
     * Helpers to decrypt retrieved encrypted data
     *
     * @return decrypted accounts
     */
    private synchronized List<SipAccountData> getDecryptedConfiguredAccounts(List<SipAccountData> accounts) {
        for (int i = 0; i < accounts.size(); i++) {
            accounts.get(i).setUsername(decrypt(accounts.get(i).getUsername()));
            accounts.get(i).setPassword(decrypt(accounts.get(i).getPassword()));
        }
        return accounts;
    }

    private List<SipAccountData> getAccounts(String accounts) {
        if (accounts.isEmpty() || accounts.equals("[]")) {
            return new ArrayList<>();
        } else {
            Type listType = new TypeToken<ArrayList<SipAccountData>>() {
            }.getType();
            return gson.fromJson(accounts, listType);
        }
    }

    private synchronized boolean isEncryptionEnabled() {
        return sharedPreferences.getBoolean(PREFS_KEY_ENCRYPTION_ENABLED, false);
    }

    private void setAlias(String alias) {
        sharedPreferences.edit().putString(PREFS_KEY_KEYSTORE_ALIAS, alias).apply();
    }

    private String getAlias() {
        return sharedPreferences.getString(PREFS_KEY_KEYSTORE_ALIAS, "");
    }

    private void initCrypto(Context context, String alias) {
        Crypto.init(context, alias, false);
        encryptionHelper = EncryptionHelper.Companion.getInstance();
    }

    private SharedPreferences initializeEncryptedSharedPreferences(Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            return EncryptedSharedPreferences.create(
                    PREFS_ENCRYPTED_FILE_NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Logger.error(TAG, "Exception when settings encrypted shared preferences", e);
            return context.getSharedPreferences(PREFS_ENCRYPTED_FILE_NAME, Context.MODE_PRIVATE);
        }
    }

    private String decrypt(String data) {
        try {
            return encryptionHelper.decrypt(data);
        } catch (Exception e) {
            Logger.error(TAG, "Error while deciphering the string", e);
            return null;
        }
    }

    private void migrateFrom(SharedPreferences prefs, Context context) {
        List<SipAccountData> accounts =
                getAccounts(prefs.getString(PREFS_KEY_ACCOUNTS, ""));
        if (!accounts.isEmpty()) {
            if (isEncryptionEnabled()) {
                initCrypto(context, getAlias());
                encryptedSharedPreferences
                        .edit()
                        .putString(PREFS_KEY_ACCOUNTS, gson.toJson(getDecryptedConfiguredAccounts(accounts)))
                        .apply();
            } else {
                encryptedSharedPreferences
                        .edit()
                        .putString(PREFS_KEY_ACCOUNTS, gson.toJson(accounts))
                        .apply();
            }
        }
        encryptedSharedPreferences
                .edit()
                .putBoolean(
                        PREFS_KEY_VERIFY_SIP_SERVER_CERT,
                        prefs.getBoolean(PREFS_KEY_VERIFY_SIP_SERVER_CERT, false)
                ).apply();
        prefs.edit().remove(PREFS_KEY_ACCOUNTS).apply();
        prefs.edit().remove(PREFS_KEY_ENCRYPTION_ENABLED).apply();
        prefs.edit().remove(PREFS_KEY_KEYSTORE_ALIAS).apply();
        prefs.edit().remove(PREFS_KEY_VERIFY_SIP_SERVER_CERT).apply();
    }

    public static SharedPreferencesHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SharedPreferencesHelper(context);
        }
        return INSTANCE;
    }

    /**
     * This method is used to save string value in shared prefrences.
     *
     * @param key Shared pref key
     */
    public void putInSharedPreference(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * This method is used to save integer value in shared preferences.
     *
     * @param key Shared pref key
     */
    public void putInSharedPreference(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * This method is used to save boolean value in shared preferences.
     *
     * @param key Shared pref key
     */
    public void putInSharedPreference(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * This method is used to get shared pref string values on the basis of key.
     *
     * @param key Shared pref key
     * @return string value from shared pref
     */
    public String getStringSharedPreference(String key) {
        return sharedPreferences.getString(key, "");
    }

    /**
     * This method is used to get shared pref integer values on the basis of key.
     *
     * @param key Shared pref key
     * @return integer value from shared pref
     */
    public int getIntSharedPreference(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    /**
     * This method is used to get shared pref boolean on the basis of key.
     *
     * @param key          Shared pref key
     * @param defaultValue default value if value doesn't exist
     * @return boolean value from shared pref
     */
    public boolean getBooleanPreference(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * This method is used to get secure protocol from shared prefs.
     */
    public boolean isSecureProtocol() {
        return getBooleanPreference(SharedPreferenceConstant.PROTOCOL, false);
    }

    public String getAccountID() {
        return getStringSharedPreference(SharedPreferenceConstant.SIP_ACCOUNT_ID);
    }

    /**
     * This method is used to clear all shared prefs data.
     */
    public void clearAllSharedPreferences() {
        sharedPreferences.edit().clear().apply();
    }
}

