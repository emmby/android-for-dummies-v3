package com.dummies.tasks.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.dummies.tasks.R;

/**
 * Our app's starting activity, which will detect whether we're on a
 * tablet or on a phone and launch the right activity as appropriate.
 */
public class LaunchActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launch the phone or tablet activity, as appropriate
        startActivity(new Intent(this,
                getResources().getBoolean(R.bool.isTablet)
                        ? TaskListAndEditorActivity.class
                        : TaskListActivity.class));

        // Finish this launcher, we don't need it anymore
        finish();
    }

}
