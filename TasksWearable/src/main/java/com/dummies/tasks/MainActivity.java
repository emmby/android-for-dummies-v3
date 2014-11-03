package com.dummies.tasks;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

/**
 * The main activity for this app.
 * This activity will implement the DataApi.DataListener interface to
 * be get notified of sync events.  It will also implement the
 * GoogleApiClient ConnectionCallbacks and OnConnectionFailedListener
 * to handle connectivity events.
 *
 * If you're writing an app that needs to sync in the background (ours
 * doesn't), then consider using a WearableListenerService to run
 * continuously in the background.
 */
public class MainActivity extends Activity
    implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    /**
     * Wearables use the GoogleApiClient to communicate with their host
     * devices (usually your phone).
     */
    GoogleApiClient googleApiClient;

    /**
     * A WearableListAdapter that knows how to read the list of tasks.
     */
    WearableTaskListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the GoogleApiClient.
        // Tell it that we need the Wearable.API, and also that
        // it should call us back on "this" for any connectivity
        // notifications.
        googleApiClient = new GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();

        // Set up the adapter
        adapter = new WearableTaskListAdapter(this);

        // Find the WearableListView in our view hiearchy
        WearableListView listView =
            (WearableListView) findViewById(R.id.list);

        // Link the adapter and the listView
        listView.setAdapter(adapter);
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
        Log.d("MainActivity", "onConnected");

        // Update the adapter as soon as we're connected.
        updateList();

        // Subscribe for any more data updates.  We will be called back
        // on onDataChanged if anything is updated.
        Wearable.DataApi.addListener(googleApiClient, this);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d("MainActivity", "onDataChanged");

        // Always release the dataEvents when we're done.  In this
        // case, we don't use the dataEvents directly,
        // so release them right away.
        dataEvents.release();

        // We were told there was an update, so update our adapter.
        updateList();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Just log a message.  We don't have to do anything at all,
        // but a log message can help us debug any issues.
        Log.d("MainActivity", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Just log a message.  We don't have to do anything at all,
        // but a log message can help us debug any issues.
        Log.d("MainActivity", "onConnectionFailed");
    }

    private void updateList() {
        // TODO is getDataItems pulling from a local store,
        // or resyncing across the network?  If going across network,
        // may need to rethink this

        // Retrieve the complete list of dataitems using DataApi
        // .getDataItems.  Because this may involve a network sync and
        // may take some time, get the results back in a ResultCallback
        // at a later time.
        Wearable.DataApi.getDataItems(googleApiClient).setResultCallback(
            new ResultCallback<DataItemBuffer>() {
                @Override
                public void onResult(DataItemBuffer dataItems) {
                    try {
                        // Before you start using dataItems,
                        // you must "freeze" them to make sure they
                        // don't change while you are iterating over them.
                        List<DataItem> items
                            = FreezableUtils.freezeIterable(dataItems);

                        // Update the adapter with the new items.
                        adapter.setResults(items);

                        Log.d("MainActivity", "adapter.setResults");
                    } finally {

                        // Always release the dataItems when you are
                        // through.
                        dataItems.release();
                    }
                }
            });
    }
}
