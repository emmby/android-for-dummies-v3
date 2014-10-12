package com.dummies.wearable;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;

import java.util.List;

public class WearableTaskListAdapter
        extends WearableListView.Adapter {

    static final java.lang.String COLUMN_TITLE = "title";

    List<DataItem> dataItems;
    LayoutInflater inflater;

    public WearableTaskListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(
                inflater.inflate(R.layout.item_task, null));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder
                                             viewHolder, int i) {

        DataMap map = ((PutDataMapRequest)dataItems.get(i)).getDataMap();

        ((ViewHolder)viewHolder).titleView.setText(
                map.getString(COLUMN_TITLE)
        );
    }

    @Override
    public int getItemCount() {
        return dataItems!=null ? dataItems.size() : 0;
    }

    public void setResults(List<DataItem> dataItems) {
        this.dataItems = dataItems;
        notifyDataSetChanged();
    }


    static class ViewHolder extends WearableListView.ViewHolder {
        TextView titleView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.title);
        }

    }
}
