package com.dummies.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;

import java.util.List;

/**
 * A WearableListAdapter that knows how to display our Task items in a
 * list.
 */
public class WearableTaskListAdapter
    extends WearableListView.Adapter
{

    // This must match the name used in the phone app
    static final String COLUMN_TITLE = "title";

    // The current list of dataItems
    List<DataItem> dataItems;

    // The layout inflater
    LayoutInflater inflater;

    public WearableTaskListAdapter(Context context) {
        // Retrieve a LayoutInflater from the current context
        inflater = LayoutInflater.from(context);
    }

    /**
     * Create a ViewHolder that will hold a reference to the views that
     * we will need to update for each new item in the list.
     */
    @SuppressLint("InflateParams")
    @Override
    public WearableListView.ViewHolder onCreateViewHolder(
        ViewGroup viewGroup, int i) {

        // Return a new ViewHolder (see below for class ViewHolder).
        // Each view in our list will use the item_task.xml layout.
        return new ViewHolder(
            inflater.inflate(R.layout.item_task, null));
    }

    /**
     * Update the views in the ViewHolder using the information in the
     * item in position i.
     */
    @Override
    public void onBindViewHolder(
        WearableListView.ViewHolder viewHolder, int i) {

        // Find the DataItem for the item in position i.
        DataItem dataItem = dataItems.get(i);

        // Reconstruct the original DataMap for that item
        DataMap map
            = DataMapItem.fromDataItem(dataItem).getDataMap();

        // Set the title view text based on the COLUMN_TITLE in the
        // DataMap
        ((ViewHolder) viewHolder).titleView.setText(
            map.getString(COLUMN_TITLE)
        );
    }

    @Override
    public int getItemCount() {
        return dataItems != null ? dataItems.size() : 0;
    }

    /**
     * Update the items in the list, and notify listeners
     * (particularly the ListView) that the data in the adapter has
     * changed.
     */
    public void setResults(List<DataItem> dataItems) {
        this.dataItems = dataItems;
        notifyDataSetChanged();
    }


    /**
     * As simple ViewHolder that just holds the titleView for out list
     * item.
     */
    static class ViewHolder extends WearableListView.ViewHolder {
        TextView titleView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.title);
        }

    }
}
