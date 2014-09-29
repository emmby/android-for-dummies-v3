package com.dummies.wearable;

import android.app.FragmentManager;
import android.content.Context;
import android.support.wearable.view.FragmentGridPagerAdapter;

public class TasksGridPagerAdapter extends FragmentGridPagerAdapter {

    Context context;

    public TasksGridPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

}
