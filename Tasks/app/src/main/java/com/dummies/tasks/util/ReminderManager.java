package com.dummies.tasks.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.dummies.tasks.provider.TaskProvider;
import com.dummies.tasks.receiver.OnAlarmReceiver;

import java.util.Calendar;

/**
 * A helper class that knows how to set reminders using the AlarmManager
 */
public class ReminderManager {

    private Context context;
    private AlarmManager alarmManager;

    public ReminderManager(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
    }

    public void setReminder(Long taskId, Calendar when) {

        // Create an intent for our OnAlarmReceiver,
        // which will show the notification when it is called
        Intent i = new Intent(context, OnAlarmReceiver.class);
        i.putExtra(TaskProvider.COLUMN_TASKID, (long) taskId);

        // Create the PendingIntent that will wrap the
        // above intent.  All intents that are used in
        // the AlarmManager must be wrapped in a PendingIntent to "give
        // permission" to the AlarmManager to call back into our
        // application.
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

        // Set the alarm
        alarmManager.set(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(),
                pi);
    }
}
