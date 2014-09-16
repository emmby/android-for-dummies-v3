package com.dummies.silentmodetoggle;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

    AudioManager audioManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        Button toggleButton = (Button) findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isPhoneSilent()) {
                    // Change back to normal mode
                    audioManager.setRingerMode(
                            AudioManager.RINGER_MODE_NORMAL);
                } else {
                    // Change to silent mode
                    audioManager.setRingerMode(
                            AudioManager.RINGER_MODE_SILENT);
                }

                // Now toggle the UI again
                updateUi();
            }
        });

        Log.d("SilentModeApp", "This is a test");
    }

    /**
     * Returns whether the phone is currently in silent mode.
     */
    private boolean isPhoneSilent() {
        return audioManager.getRingerMode()
                == AudioManager.RINGER_MODE_SILENT;
    }

    /**
     * Updates the UI image to show silent or normal, as appropriate
     */
    private void updateUi() {
        ImageView imageView = (ImageView) findViewById(R.id.phone_icon);

        int phoneImage = isPhoneSilent()
                ? R.drawable.phone_on
                : R.drawable.phone_silent;

        imageView.setImageDrawable(getDrawable(phoneImage));
    }

    /**
     * Every time the activity is resumed, make sure to update the
     * buttons to reflect the current state of the system (since the
     * user may have changed the phone's silent state while we were in
     * the background).
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
    }
}