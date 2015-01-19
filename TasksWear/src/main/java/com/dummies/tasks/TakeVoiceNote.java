package com.dummies.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.nio.charset.Charset;

import static com.google.android.gms.wearable.NodeApi
    .GetConnectedNodesResult;

public class TakeVoiceNote extends Activity
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private static final String PATH_ADD_TASK = "/addTask";

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
        Log.d("TakeVoiceNote", "onConnected");
        
        // The speech recognition text is passed in via the intent
        String voiceNote = getIntent().getStringExtra(
            Intent.EXTRA_TEXT);

        final byte[] voiceNoteBytes =
            voiceNote.getBytes(Charset.forName("utf-8"));


        Wearable.NodeApi.getConnectedNodes(googleApiClient)
            .setResultCallback(
                new ResultCallback<GetConnectedNodesResult>() {
                    @Override
                    public void onResult(GetConnectedNodesResult nodes) {
                        for (Node node : nodes.getNodes()) {
                            
                            // Send the phone a message requesting that
                            // it add the task to the database
                            Wearable.MessageApi.sendMessage(
                                googleApiClient,
                                node.getId(),
                                PATH_ADD_TASK,
                                voiceNoteBytes
                                );
                            
                        }
                        
                        finish();
                    }
                }
            );
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Just log a message.  We don't have to do anything at all,
        // but a log message can help us debug any issues.
        Log.d("TakeVoiceNote", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Just log a message.  We don't have to do anything at all,
        // but a log message can help us debug any issues.
        Log.d("TakeVoiceNote", "onConnectionFailed");
    }

}
