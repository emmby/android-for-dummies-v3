package com.dummies.wearable;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity {
    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        final Resources resources = getResources();
        final GridViewPager pager =
                (GridViewPager) findViewById(R.id.pager);

        pager.setOnApplyWindowInsetsListener(
            new OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v,
                                                    WindowInsets insets) {

                    pager.setPageMargins(
                            resources.getDimensionPixelOffset(
                                    R.dimen.page_row_margin),
                            resources.getDimensionPixelOffset(
                                    R.dimen.page_column_margin));

                    return insets;
                }
            });

        pager.setAdapter(
                new TasksGridPagerAdapter(this, getFragmentManager()));
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
}
