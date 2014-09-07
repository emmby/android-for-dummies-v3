package com.dummies.tasks.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dummies.tasks.R;
import com.dummies.tasks.activity.TaskEditActivity;
import com.dummies.tasks.provider.TaskProvider;

/**
 * This class is called when our reminder alarm fires,
 * at which point we'll create a notification and show it to the user.
 */
public class OnAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Note: Do not do any asynchronous operations in
        // BroadcastReceive.onReceive. If you need to do asynchronous
        // operations (eg. network requests, disk or database reads or
        // writes, etc.), then update OnAlarmReceiver to subclass
        // android.support.v4.content.WakefulBroadcastReceiver and
        // create a new service to do all of your heavy lifting.
        // Remember to call startWakefulService to start your service,
        // and remember to call
        // WakefulBroadcastReceiver.completeWakefulIntent from your
        // service when you are done.

        NotificationManager mgr = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the intent that will open the TaskEditActivity
        // for the specified task id.  We get the id of the task
        // from the OnAlarmReceiver's broadcast intent.
        Intent taskEditIntent =
                new Intent(context, TaskEditActivity.class);
        long taskId = intent.getLongExtra(TaskProvider.COLUMN_TASKID, -1);
        String title = intent.getStringExtra(TaskProvider.COLUMN_TITLE);
        taskEditIntent.putExtra(TaskProvider.COLUMN_TASKID, taskId);

        // Create the PendingIntent that will wrap the
        // taskEditIntent.  All intents that are used in
        // notifications must be wrapped in a PendingIntent to "give
        // permission" to the OS to call back into our
        // application when the notification is clicked.
        PendingIntent pi = PendingIntent.getActivity(context, 0,
                taskEditIntent, PendingIntent.FLAG_ONE_SHOT);

        // Build the Notification object using a Notification.Builder
        Notification note = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string
                        .notify_new_task_title))
                .setContentText(title)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        // Send the notification.
        mgr.notify((int) taskId, note);
    }
}
