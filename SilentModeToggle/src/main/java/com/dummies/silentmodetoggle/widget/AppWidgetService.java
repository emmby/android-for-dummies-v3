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
    public static String ACTION_DO_TOGGLE = "actionDoToggle";

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

        if( intent!=null && intent.getBooleanExtra(
                ACTION_DO_TOGGLE,false)) {
            performToggle();
        }

        ComponentName me = new ComponentName(this, AppWidget.class);
        AppWidgetManager mgr = AppWidgetManager.getInstance(this);
        mgr.updateAppWidget(me, updateUi());
    }

    private void performToggle() {
        audioManager.setRingerMode(
                isPhoneSilent()
                        ? AudioManager.RINGER_MODE_NORMAL
                        : AudioManager.RINGER_MODE_SILENT);
    }

    private RemoteViews updateUi() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.app_widget);

        int phoneImage = isPhoneSilent()
                ? R.drawable.phone_state_silent
                : R.drawable.phone_state_normal;

        remoteViews.setImageViewResource(R.id.phone_state, phoneImage);

        // Create an Intent to toggle the phone's state
        Intent intent = new Intent(this, AppWidgetService.class)
                .putExtra(ACTION_DO_TOGGLE,true);
        PendingIntent pendingIntent =
                PendingIntent.getService(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);

        // Get the layout for the App Widget and attach an on-click
        // listener to the button
        remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

        return remoteViews;
    }

    /**
     * Returns whether the phone is currently in silent mode.
     */
    private boolean isPhoneSilent() {
        return audioManager.getRingerMode()
                == AudioManager.RINGER_MODE_SILENT;
    }


}
