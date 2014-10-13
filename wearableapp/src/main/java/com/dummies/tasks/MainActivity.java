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

public class MainActivity extends Activity
    implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    GoogleApiClient googleApiClient;
    WearableTaskListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleApiClient = new GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();

        adapter = new WearableTaskListAdapter(this);

        WearableListView listView =
            (WearableListView) findViewById(R.id.list);

        listView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        updateList(); // TODO it would be more efficient to only update
                      // the changed items
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("MainActivity", "onConnected");
        updateList();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("MainActivity", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("MainActivity", "onConnectionFailed");
    }

    private void updateList() {
        // TODO is getDataItems pulling from a local store,
        // or resyncing across the network?  If going across network,
        // may need to rethink this
        Wearable.DataApi.getDataItems(googleApiClient).setResultCallback(
            new ResultCallback<DataItemBuffer>() {
                @Override
                public void onResult(DataItemBuffer dataItems) {
                    List<DataItem> items
                        = FreezableUtils.freezeIterable(dataItems);
                    adapter.setResults(items);
                }
            });
    }
}
