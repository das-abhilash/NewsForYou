package com.example.abhilash.newsforyou;

import android.content.Context;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Abhilash on 6/29/2016.
 */

public class WearableAdapter extends WearableListView.Adapter {
    private String[] mItems;
    private final LayoutInflater mInflater;

    public WearableAdapter(Context context, String [] items) {
        mInflater = LayoutInflater.from(context);
        mItems = items;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int i) {
        return new ItemViewHolder(mInflater.inflate(R.layout.list_item,viewGroup));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder viewHolder,
                                 int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        CircledImageView circledView = itemViewHolder.mCircledImageView;
        circledView.setImageResource(R.drawable.ic_navigate_before_white_24dp);
        TextView textView = itemViewHolder.mItemTextView;
        textView.setText(mItems[position + 1] );
        circledView.setContentDescription(mItems[position + 1]);
    }

    @Override
    public int getItemCount() {
        return mItems.length;
    }

    private static class ItemViewHolder extends WearableListView.ViewHolder {
        private CircledImageView mCircledImageView;
        private TextView mItemTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mCircledImageView = (CircledImageView)
                    itemView.findViewById(R.id.circle);
            mItemTextView = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
