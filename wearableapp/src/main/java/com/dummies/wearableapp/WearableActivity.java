package com.dummies.wearableapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

public class WearableActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wearable);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

//        PendingResult<DataItemBuffer> results = Wearable.DataApi.getDataItems(mGoogleApiClient);
//        results.setResultCallback(new ResultCallback<DataItemBuffer>() {
//            @Override
//            public void onResult(DataItemBuffer dataItems) {
//                try {
//                    if (dataItems.getCount() != 0) {
//                        DataMapItem dataMapItem = DataMapItem.fromDataItem
//                                (dataItems.get(0));
//
//                        // This should read the correct value.
//                        int value = dataMapItem.getDataMap().getInt
//                                (COUNT_KEY);
//                    }
//                } finally {
//                    dataItems.release();
//                }
//            }
//        });
    }
}
