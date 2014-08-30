package com.dummies.android.taskreminder;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public abstract class WakeReminderIntentService extends IntentService {
    abstract void doReminderWork(Intent intent);

    private static final String LOCK_NAME_STATIC = "com.dummies.android.taskreminder.Static";
    private static PowerManager.WakeLock WAKE_LOCK = null;

    public static void acquireStaticLock(Context context) {
        getLock(context).acquire();
    }

    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (WAKE_LOCK == null) {
            PowerManager mgr = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            WAKE_LOCK = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    LOCK_NAME_STATIC);
        }
        return (WAKE_LOCK);
    }

    public WakeReminderIntentService(String name) {
        super(name);
    }

    @Override
    final protected void onHandleIntent(Intent intent) {
        try {
            doReminderWork(intent);
        } finally {
            getLock(this).release();
        }
    }
}
