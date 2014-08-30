package com.dummies.android.taskreminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

public class ReminderService extends WakeReminderIntentService {

    public ReminderService() {
        super("ReminderService");
    }

    @Override
    void doReminderWork(Intent intent) {
        long rowId = intent.getExtras().getLong(ReminderProvider.COLUMN_ROWID);

        NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, ReminderEditActivity.class);
        notificationIntent.putExtra(ReminderProvider.COLUMN_ROWID, rowId);

        PendingIntent pi = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        Notification note = new Notification.Builder(this)
                .setContentTitle(getString(R.string.notify_new_task_title))
                .setContentText(getString(R.string.notify_new_task_message))
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        // An issue could occur if user ever enters over 2,147,483,647 (max int value) tasks.
        mgr.notify((int) rowId, note);

    }
}
