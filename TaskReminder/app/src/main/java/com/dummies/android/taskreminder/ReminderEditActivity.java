package com.dummies.android.taskreminder;

public class ReminderEditActivity extends SingleFragmentActivity implements
        OnFinishEditor {

    public ReminderEditActivity() {
        super(ReminderEditFragment.class);
    }

    @Override
    public void finishEditor() {
        finish();
    }
}
