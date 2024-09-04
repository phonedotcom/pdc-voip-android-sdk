/*
 * Copyright (c) 2023 Phone.com®, All Rights Reserved.
 */
package com.phone.sip;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;

public final class MediaPlayerController {

    public static final String TAG = MediaPlayerController.class.getSimpleName();
    private static MediaPlayerController INSTANCE = null;
    private final AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;

    private MediaPlayerController() {
        throw new UnsupportedOperationException("This is a controller class and cannot be instantiated");
    }

    private MediaPlayerController(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public static synchronized MediaPlayerController getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = new MediaPlayerController(context);
        return INSTANCE;
    }

    public void stopMusicPlayer() {
        if (Build.VERSION.SDK_INT >= 26) {
            final AudioAttributes mPlaybackAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
                    .setAudioAttributes(mPlaybackAttributes)
                    .setAcceptsDelayedFocusGain(false)
                    .setWillPauseWhenDucked(true)
                    .setOnAudioFocusChangeListener(i -> {

                    }, new Handler())
                    .build();
            audioManager.requestAudioFocus(audioFocusRequest);
            Logger.debug(TAG, "Stop Music Player - With Audio Focus");
        } else {
            audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            Logger.debug(TAG, "Stop Music Player - W/O Audio Focus");
        }
    }

    public void resumeMusicPlayer() {
        if (Build.VERSION.SDK_INT >= 26 && audioFocusRequest != null) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest);
            Logger.debug(TAG, "Resume Music Player - With Audio Focus");
        } else {
            audioManager.abandonAudioFocus(null);
            Logger.debug(TAG, "Resume Music Player - W/O Audio Focus");
        }
    }
}
