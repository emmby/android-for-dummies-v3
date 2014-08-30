package com.dummies.android.taskreminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static android.content.Context.NOTIFICATION_SERVICE;

public class OnAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Note: Do not do any asynchronous operations in BroadcastReceive.onReceive.
        // If you need to do asynchronous operations (eg. network requests,
        // disk or database reads or writes, etc.), then update OnAlarmReceiver
        // to subclass android.support.v4.content.WakefulBroadcastReceiver
        // and create a new service to do all of your heavy lifting.  Remember
        // to call startWakefulService to start your service, and remember to
        // call WakefulBroadcastReceiver.completeWakefulIntent from your service
        // when you are done.

        long taskId = intent.getExtras().getLong(ReminderProvider.COLUMN_TASKID);

        NotificationManager mgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, ReminderEditActivity.class);
        notificationIntent.putExtra(ReminderProvider.COLUMN_TASKID, taskId);

        PendingIntent pi = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        Notification note = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.notify_new_task_title))
                .setContentText(context.getString(R.string.notify_new_task_message))
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        // An issue could occur if user ever enters over 2,147,483,647 (max int value) tasks.
        mgr.notify((int) taskId, note);


    }
}
