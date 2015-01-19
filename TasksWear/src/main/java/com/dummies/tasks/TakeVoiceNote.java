package com.dummies.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class TakeVoiceNote extends Activity
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    public static final String COLUMN_TASKID = "_id";
    public static final String COLUMN_DATE_TIME = "task_date_time";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_TITLE = "title";

    // Google Play Constants
    private static final String PLAY_BASE_URL = "/tasks";


    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleApiClient = new GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();


    }

    @Override
    protected void onStart() {
        super.onStart();

        // Connect to the GoogleApiClient.  When we are connected,
        // we will receive a callback on onConnected().
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // We're done, so disconnect from the GoogleApiClient.
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("TakeVoiceActivity", "onConnected");
        
        // The speech recognition text is passed in via the intent
        final String voiceNote = getIntent().getStringExtra(
            Intent.EXTRA_TEXT);
        
        PendingResult<DataItemBuffer> result =
            Wearable.DataApi.getDataItems(googleApiClient);
        
        result.setResultCallback(
            new ResultCallback<DataItemBuffer>() {
                @Override
                public void onResult(DataItemBuffer existingTasks) {
                    List<DataItem> items
                        = FreezableUtils.freezeIterable(existingTasks);

                    // Get the highest ID currently in the DB
                    DataItem item = items.get(items.size() - 1);
                    DataMap previousMap
                        = DataMapItem.fromDataItem(item).getDataMap();
                    long highestPreviousId 
                        = previousMap.getLong(COLUMN_TASKID);

                    // Create a new task with an ID that's one higher
                    // than the previous highest ID.
                    long id = highestPreviousId + 1;
                    PutDataMapRequest dataMap = PutDataMapRequest.create(
                        PLAY_BASE_URL + "/" + id);
                    DataMap map = dataMap.getDataMap();
                    map.putLong(COLUMN_TASKID, id);
                    map.putString(COLUMN_TITLE, voiceNote);
//                    map.putLong(COLUMN_DATE_TIME, values.getAsLong(COLUMN_DATE_TIME));
                    map.putString(COLUMN_NOTES, "Voice note");
                    PutDataRequest request = dataMap.asPutDataRequest();
                    Wearable.DataApi.putDataItem(googleApiClient, request);

                    // Show the new task to the user
                    startActivity( new Intent(TakeVoiceNote.this,
                            MainActivity.class));
                    finish();
                }
            }
        );

    }

    @Override
    public void onConnectionSuspended(int i) {
        // Just log a message.  We don't have to do anything at all,
        // but a log message can help us debug any issues.
        Log.d("TakeVoiceActivity", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Just log a message.  We don't have to do anything at all,
        // but a log message can help us debug any issues.
        Log.d("TakeVoiceActivity", "onConnectionFailed");
    }

}
