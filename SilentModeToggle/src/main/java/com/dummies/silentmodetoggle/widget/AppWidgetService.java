package com.dummies.silentmodetoggle.widget;

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.RemoteViews;

import com.dummies.silentmodetoggle.R;

public class AppWidgetService extends IntentService {

    AudioManager audioManager;

    public AppWidgetService() {
        super("AppWidgetService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(
                Activity.AUDIO_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ComponentName me = new ComponentName(this, AppWidget.class);
        AppWidgetManager mgr = AppWidgetManager.getInstance(this);
        mgr.updateAppWidget(me, buildUpdate());
    }

    private RemoteViews buildUpdate() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.app_widget);

        audioManager.setRingerMode(
                isPhoneSilent()
                        ? AudioManager.RINGER_MODE_NORMAL
                        : AudioManager.RINGER_MODE_SILENT);

        updateUi(remoteViews);

        Intent i = new Intent(this, AppWidget.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        remoteViews.setOnClickPendingIntent(R.id.phoneState, pi);

        return remoteViews;
    }

    /**
     * Returns whether the phone is currently in silent mode.
     */
    private boolean isPhoneSilent() {
        return audioManager.getRingerMode()
                == AudioManager.RINGER_MODE_SILENT;
    }


    /**
     * Updates the UI image to show silent or normal, as appropriate
     */
    private void updateUi(RemoteViews remoteViews) {
        int phoneImage = isPhoneSilent()
                ? R.drawable.phone_state_silent
                : R.drawable.phone_state_normal;

        remoteViews.setImageViewResource(R.id.phoneState, phoneImage);
    }
}
