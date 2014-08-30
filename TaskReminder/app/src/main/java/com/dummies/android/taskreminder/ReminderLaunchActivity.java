package com.dummies.android.taskreminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ReminderLaunchActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launch the phone or tablet activity, as appropriate,
        // then finish
        startActivity(new Intent(this,
                getResources().getBoolean(R.bool.isTablet)
                        ? ReminderListAndEditorActivity.class
                        : ReminderListActivity.class));
        finish();
    }

}
