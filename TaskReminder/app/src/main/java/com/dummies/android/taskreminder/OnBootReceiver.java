package com.dummies.android.taskreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import java.util.Calendar;

public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ReminderManager reminderMgr = new ReminderManager(context);

        Cursor cursor = context.getContentResolver().query(
                ReminderProvider.CONTENT_URI, null, null, null, null);

        if( cursor==null )
            return;

        cursor.moveToFirst();
        int taskIdColumnIndex = cursor
                .getColumnIndex(ReminderProvider.COLUMN_TASKID);
        int dateTimeColumnIndex = cursor
                .getColumnIndex(ReminderProvider.COLUMN_DATE_TIME);

        while (!cursor.isAfterLast()) {

            long taskId = cursor.getLong(taskIdColumnIndex);
            long dateTime = cursor.getLong(dateTimeColumnIndex);

            Calendar cal = Calendar.getInstance();
            cal.setTime(new java.util.Date(dateTime));

            reminderMgr.setReminder(taskId, cal);

            cursor.moveToNext();
        }
        cursor.close();
    }
}
