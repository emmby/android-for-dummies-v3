package com.dummies.wearable;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;

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
    implements DataApi.DataListener
{

    GoogleApiClient googleApiClient;
    WearableTaskListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleApiClient = new GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
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
