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

            int rowIdColumnIndex = cursor
                    .getColumnIndex(ReminderProvider.COLUMN_ROWID);
            int dateTimeColumnIndex = cursor
                    .getColumnIndex(ReminderProvider.COLUMN_DATE_TIME);

            while (!cursor.isAfterLast()) {

                long rowId = cursor.getLong(rowIdColumnIndex);
                long dateTime = cursor.getLong(dateTimeColumnIndex);

                Log.d(TAG, "Adding alarm from boot.");
                Log.d(TAG, "Row Id - " + rowId);
                Log.d(TAG, "Date Time - " + dateTime);

                Calendar cal = Calendar.getInstance();
                cal.setTime(new java.util.Date(dateTime));

                reminderMgr.setReminder(rowId, cal);

                cursor.moveToNext();
            }
            cursor.close();
        }
    }
}
