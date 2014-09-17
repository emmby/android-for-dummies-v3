package com.dummies.silentmodetoggle.activity;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dummies.silentmodetoggle.R;
import com.dummies.silentmodetoggle.util.RingerHelper;

public class MainActivity extends Activity {

    AudioManager audioManager;

    /**
     * This method is called to initialize the activity after the
     * java constructor for this class has been called.  This is
     * typically where you would call setContentView to inflate your
     * layout, and findViewById to initialize your views.
     * @param savedInstanceState contains additional data about the
     *                           saved state of the activity,
     *                           if it was previously shutdown and is
     *                           now being re-created from saved state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Always call super.onCreate() first.
        super.onCreate(savedInstanceState);

        // Get a reference to Android's AudioManager so we can use
        // it to toggle our ringer.
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Initialize our layout using the res/layout/activity_main.xml
        // layout file that contains our views for this activity.
        setContentView(R.layout.activity_main);

        // Find the view named "content" in our layout file.
        View contentView = findViewById(R.id.content);

        // Create a click listener for the contentView that will toggle
        // the phone's ringer state, and then update the UI to reflect
        // the new state.
        contentView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Toggle the ringer mode.  If it's currently normal,
                // make it silent.  If it's currently silent,
                // do the opposite.
                RingerHelper.performToggle(audioManager);

                // Update the UI to reflect the new state
                updateUi();
            }
        });

        // This is how you log messages to adb logcat!
        Log.d("SilentModeToggle", "This is a test");
    }


    /**
     * Updates the UI image to show an image representing silent or
     * normal, as appropriate
     */
    private void updateUi() {
        // Find the view named phone_icon in our layout.  We know it's
        // an ImageView in the layout, so downcast it to an ImageView.
        ImageView imageView = (ImageView) findViewById(R.id.phone_icon);

        // Set phoneImage to the ID of image that represents ringer on
        // or off.  These are found in res/drawable-xhdpi
        int phoneImage = RingerHelper.isPhoneSilent(audioManager)
                ? R.drawable.ringer_off
                : R.drawable.ringer_on;

        // Set the imageView to the image in phoneImage
        imageView.setImageResource(phoneImage);
    }

    /**
     * Every time the activity is resumed, make sure to update the
     * buttons to reflect the current state of the system (since the
     * user may have changed the phone's silent state while we were in
     * the background).
     *
     * Visit http://d.android.com/reference/android/app/Activity.html
     * for more information about the Android Activity lifecycle.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Update our UI in case anything has changed.
        updateUi();
    }
}