package com.dummies.android.taskreminder;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver {

    private static final String TAG = OnBootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        ReminderManager reminderMgr = new ReminderManager(context);

        Cursor cursor = context.getContentResolver().query(
                ReminderProvider.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            int taskIdColumnIndex = cursor
                    .getColumnIndex(ReminderProvider.COLUMN_TASKID);
            int dateTimeColumnIndex = cursor
                    .getColumnIndex(ReminderProvider.COLUMN_DATE_TIME);

            while (!cursor.isAfterLast()) {

                long taskId = cursor.getLong(taskIdColumnIndex);
                long dateTime = cursor.getLong(dateTimeColumnIndex);

                Log.d(TAG, "Adding alarm from boot.");
                Log.d(TAG, "Task Id - " + taskId);
                Log.d(TAG, "Date Time - " + dateTime);

                Calendar cal = Calendar.getInstance();
                cal.setTime(new java.util.Date(dateTime));

                reminderMgr.setReminder(taskId, cal);

                cursor.moveToNext();
            }
            cursor.close();
        }
    }
}
