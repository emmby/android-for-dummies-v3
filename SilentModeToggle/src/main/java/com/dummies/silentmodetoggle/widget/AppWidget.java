package com.dummies.silentmodetoggle.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * The main class that represents our app's widget.
 * Dispatches to a service to do all of the heavy lifting.
 */
public class AppWidget extends AppWidgetProvider {

    /**
     * Called when the widget is first created, and every update period
     * as defined in xml/widget_provider.xml
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager
            appWidgetManager, int[] appWidgetIds) {

        // Start a service so the service can take on updating the
        // widget without having to worry about how long the responses
        // take to generate.  This is necessary for any widgets that do
        // any sort of I/O (network, disk, etc).  Our widget doesn't do
        // I/O so using a service is not strictly-speaking necessary,
        // but it's a very common practice and it's important to know
        // how to use it.
        context.startService(new Intent(context, AppWidgetService.class));
    }

}
