package com.dummies.silentmodetoggle.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

public class AppWidget extends AppWidgetProvider {

    @Override
    public void onReceive(@NonNull Context ctxt, @NonNull Intent intent) {

        if (intent.getAction() == null) {
            ctxt.startService(new Intent(ctxt, AppWidgetService.class));
        } else {
            super.onReceive(ctxt, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager
            appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, AppWidgetService.class));
    }

}
