package com.dummies.tasks.service;

import android.content.ContentValues;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.Charset;

import static com.dummies.tasks.provider.TaskProvider.COLUMN_DATE_TIME;
import static com.dummies.tasks.provider.TaskProvider.COLUMN_NOTES;
import static com.dummies.tasks.provider.TaskProvider.COLUMN_TITLE;
import static com.dummies.tasks.provider.TaskProvider.CONTENT_URI;

public class AddTaskWearableListenerService extends 
    WearableListenerService 
{
    private static final String PATH_ADD_TASK = "/addTask";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if( PATH_ADD_TASK.equals(messageEvent.getPath())) {
            String title = new String(messageEvent.getData(),
                Charset.forName("utf-8"));
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, title);
            values.put(COLUMN_NOTES, "");
            values.put(COLUMN_DATE_TIME, System.currentTimeMillis());

            getContentResolver().insert(CONTENT_URI, values);
        }
    }
}
