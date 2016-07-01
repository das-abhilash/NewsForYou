package com.example.abhilash.newsforyou.service;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.abhilash.newsforyou.R;
import com.example.abhilash.newsforyou.data.NewsColumns;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;




public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    View mEmptyViewText;
    View mEmptyViewImage;

    public NewsAdapter(Context context,Cursor cursor,View emptyViewText,View emptyViewImage) {
        mCursor = cursor;
        mContext = context;
        mEmptyViewText = emptyViewText;
        mEmptyViewImage =   emptyViewImage;
    }
    public NewsAdapter(){

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        return vh;
    }




        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.name.setText(mCursor.getString(mCursor.getColumnIndex(NewsColumns.NAME)));
            holder.provider.setText(mCursor.getString(mCursor.getColumnIndex(NewsColumns.PROVIDER)));

            Picasso.with(mContext).load(mCursor.getString(mCursor.getColumnIndex(NewsColumns.IMAGE))).placeholder(R.mipmap.ic_empty).into(holder.newsImage);
            holder.newsImage.setContentDescription(mCursor.getString(mCursor.getColumnIndex(NewsColumns.NAME)));
            String publishedDate = mCursor.getString(mCursor.getColumnIndex(NewsColumns.TIME));
            Calendar c = Calendar.getInstance();

            Date er = c.getTime();
           SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy",Locale.UK);

            String sd =publishedDate.substring(8,10)+" "+publishedDate.substring(5,7)+" " + publishedDate.substring(0,4)
                    +" "+ publishedDate.substring(11,19);

            SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy hh:mm:ss", Locale.UK);
            Date newDate = null;
            try {
                newDate = format.parse(sd);

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            String timeAgo = null;
            long different = er.getTime() - newDate.getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;
            long elapsedDays;
            long elapsedHours;
            long elapsedMinutes;
            long elapsedSeconds;

            if(( elapsedDays = different / daysInMilli)!= 0) {
                different = different % daysInMilli;

                timeAgo = (elapsedDays == 1?" -" +String.valueOf(elapsedDays)+ " day ago"
                        : " - " +String.valueOf(elapsedDays)+ " days ago");//" -" +String.valueOf(elapsedDays)+ " days ago";
            } else if((elapsedHours = different / hoursInMilli)!= 0)
            {
                different = different % hoursInMilli;
                timeAgo = " - " +String.valueOf(elapsedHours)+ " hours ago";
            } else if((elapsedMinutes = different / minutesInMilli)!= 0)
            {
                different = different % minutesInMilli;
                timeAgo =" - " + String.valueOf(elapsedMinutes)+ " minutes ago";
            } else if((elapsedSeconds = different / secondsInMilli)!= 0)
            {
                different = different % minutesInMilli;
                timeAgo = " - " +String.valueOf(elapsedSeconds)+ " seconds ago";
            }

            holder.time.setText(timeAgo);


          }

    @Override
    public int getItemCount () {
            int count = mCursor.getCount();
     mEmptyViewText.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        mEmptyViewImage.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        return count;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements RecyclerItemClickListener.OnItemClickListener{
        public ImageView newsImage;
        public TextView name;
        public TextView provider;
        public TextView time;

        public ViewHolder(View view) {
            super(view);
            newsImage = (ImageView) view.findViewById(R.id.newsImage);
            name = (TextView) view.findViewById(R.id.name);
            provider = (TextView) view.findViewById(R.id.provider);
            time = (TextView) view.findViewById(R.id.time);
        }

        @Override
        public void onItemClick(View view, int position) {

        }
    }
}

