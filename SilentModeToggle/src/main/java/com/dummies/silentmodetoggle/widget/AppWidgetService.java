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
import com.dummies.silentmodetoggle.util.RingerHelper;

/**
 * The service that will handle all of our widget's operations.  The
 * intent sent to the service will tell it what we want to do.
 *
 * This is an instance of IntentService.  An IntentService is a
 * convenient way to handle things that need to be done on background
 * threads.  Whenever a new intent is received, onHandleIntent will be
 * executed in a background thread.  This will allow you to perform
 * whatever operations you want to in the background,
 * no matter how long they might take, without blocking the foreground
 * UI thread (which would cause the app to hang).
 */
public class AppWidgetService extends IntentService {

    // A flag that we'll set in our intent whenever we want to indicate
    // that we wish to toggle the phone's silent setting
    private static String ACTION_DO_TOGGLE = "actionDoToggle";

    AudioManager audioManager;

    /**
     * All IntentServices need to have a name.  Ours is called
     * "AppWidgetService"
     */
    public AppWidgetService() {
        super("AppWidgetService");
    }

    /**
     * This is called when the service is initialized,
     * after the object's java constructor.
     */
    @Override
    public void onCreate() {
        // Always call super.onCreate
        super.onCreate();

        // Just like in our activity, we'll get a reference to Android's
        // AudioManager so we can use it to toggle our ringer.
        audioManager = (AudioManager) getSystemService(
                Activity.AUDIO_SERVICE);
    }

    /**
     * onHandleIntent is called on a background thread.
     * @param intent The intent to be processed
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        // Check the intent.  If it says ACTION_DO_TOGGLE,
        // then toggle the phone's silent mode.
        // If it doesn't say ACTION_DO_TOGGLE, then this is just an
        // update request so update the UI.
        if( intent!=null && intent.getBooleanExtra(
                ACTION_DO_TOGGLE,false)) {
            RingerHelper.performToggle(audioManager);
        }

        // Get a reference to Android's AppWidgetManager
        AppWidgetManager mgr = AppWidgetManager.getInstance(this);

        // Update the widget's UI
        // First, find the name for our widget,
        // then ask the AppWidgetManager to update it using the views
        // that we'll construct in update()
        ComponentName name = new ComponentName(this, AppWidget.class);
        mgr.updateAppWidget(name, updateUi());
    }

    /**
     * Returns the RemoteViews that will be used to update the widget.
     * Similar to updateUi in MainActivity, but appropriate for use
     * with widgets.
     */
    private RemoteViews updateUi() {
        // Inflate the res/layout/app_widget.xml layout file into a
        // RemoteViews object, which will be used to communicate with
        // our widget.
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.app_widget);

        // Determine which image to use in our widget
        int phoneImage = RingerHelper.isPhoneSilent(audioManager)
                ? R.drawable.icon_ringer_off
                : R.drawable.icon_ringer_on;

        // Set the appropriate image
        remoteViews.setImageViewResource(R.id.phone_state, phoneImage);

        // Create an Intent to toggle the phone's state.
        // This intent specifies ACTION_DO_TOGGLE=true,
        // which we look for in onHandleIntent.
        Intent intent = new Intent(this, AppWidgetService.class)
                .putExtra(ACTION_DO_TOGGLE,true);

        // Wrap the intent in a pending intent.  A PendingIntent gives
        // someone in another process permission to send us an intent.
        // In this case, the widget is actually running in another
        // process (the phone's launcher process),
        // so it will need to have a pending intent to communicate back
        // into our service.
        // We'll specify FLAG_ONE_SHOT to this intent to ensure it is
        // only used once.  There are some situations where a
        // PendingIntent can be automatically retried on our behalf,
        // and we want to ensure that we don't accidentally do a few
        // extra toggles.  See
        // http://d.android.com/reference/android/app/PendingIntent.html
        // for more information about pending intents.
        PendingIntent pendingIntent =
                PendingIntent.getService(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);

        // Get the layout for the App Widget and attach an on-click
        // listener to the button
        remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

        return remoteViews;
    }
}
