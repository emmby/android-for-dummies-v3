package com.dummies.silentmodetoggle.util;

import android.media.AudioManager;

public class RingerHelper {
    // private to prevent users from creating a RingerHelper object
    private RingerHelper() {}

    /**
     * Toggles the phone's silent mode
     */
    public static void performToggle(AudioManager audioManager) {
        // If the phone is currently silent, then unsilence it.  If
        // it's currently normal, then silence it.
        audioManager.setRingerMode(
                isPhoneSilent(audioManager)
                        ? AudioManager.RINGER_MODE_NORMAL
                        : AudioManager.RINGER_MODE_SILENT);
    }

    /**
     * Returns whether the phone is currently in silent mode.
     */
    public static boolean isPhoneSilent(AudioManager audioManager) {
        return audioManager.getRingerMode()
                == AudioManager.RINGER_MODE_SILENT;
    }


}
