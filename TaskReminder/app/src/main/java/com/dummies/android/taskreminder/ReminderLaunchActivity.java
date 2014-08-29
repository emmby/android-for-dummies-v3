package com.dummies.android.taskreminder;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

public class ReminderLaunchActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launch the phone or tablet activity, as appropriate, then finish
        startActivity(new Intent(this,
                isTablet() ? ReminderListAndEditorActivity.class
                        : ReminderListActivity.class));
        finish();
    }

    private boolean isTablet() {
        boolean large = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        boolean xlarge = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        return large || xlarge;
    }
}
