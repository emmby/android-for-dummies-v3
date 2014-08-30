package com.dummies.android.taskreminder;

public interface OnEditReminder {
    /**
     * Called when the user asks to edit or insert a task.
     */
    public void editReminder(long id);
}
