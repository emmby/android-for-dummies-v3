package com.dummies.android.taskreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import java.util.Calendar;

/**
 * This class will be triggered when the phone first boots so that our
 * app can re-install any alarms that need to be set.  If we didn't do
 * this, then the phone would lose all of our alarms on reboot!
 */
public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Get the cursor for our tasks
        Cursor cursor = context.getContentResolver().query(
                ReminderProvider.CONTENT_URI, null, null, null, null);

        // If our db is empty, don't do anything
        if (cursor == null)
            return;


        try {
            cursor.moveToFirst();

            // get the indices of the taskId and date_time columns
            int taskIdColumnIndex = cursor
                    .getColumnIndex(ReminderProvider.COLUMN_TASKID);
            int dateTimeColumnIndex = cursor
                    .getColumnIndex(ReminderProvider.COLUMN_DATE_TIME);

            // Loop over all of the tasks in the db
            while (!cursor.isAfterLast()) {

                // Get the id and dateTime for this task
                long taskId = cursor.getLong(taskIdColumnIndex);
                long dateTime = cursor.getLong(dateTimeColumnIndex);

                Calendar cal = Calendar.getInstance();
                cal.setTime(new java.util.Date(dateTime));

                // Set the reminder
                new ReminderManager(context).setReminder(taskId, cal);

                cursor.moveToNext();
            }

        } finally {
            // Always close the cursor when we finish!
            cursor.close();
        }
    }
}
