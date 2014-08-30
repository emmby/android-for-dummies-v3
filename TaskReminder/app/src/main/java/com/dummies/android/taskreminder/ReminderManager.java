package com.dummies.android.taskreminder;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ReminderManager {

    private Context context;
    private AlarmManager alarmManager;

    public ReminderManager(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
    }

    public void setReminder(Long taskId, Calendar when) {

        Intent i = new Intent(context, OnAlarmReceiver.class);
        i.putExtra(ReminderProvider.COLUMN_TASKID, (long) taskId);

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i,
                PendingIntent.FLAG_ONE_SHOT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pi);
    }
}
