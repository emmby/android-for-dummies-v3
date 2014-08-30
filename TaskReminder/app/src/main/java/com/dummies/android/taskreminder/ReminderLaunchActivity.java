package com.dummies.android.taskreminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Our app's starting activity, which will detect whether we're on a
 * tablet or on a phone and launch the right activity as appropriate.
 */
public class ReminderLaunchActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launch the phone or tablet activity, as appropriate
        startActivity(new Intent(this,
                getResources().getBoolean(R.bool.isTablet)
                        ? ReminderListAndEditorActivity.class
                        : ReminderListActivity.class));

        // Finish this launcher, we don't need it anymore
        finish();
    }

}
