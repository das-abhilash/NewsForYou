package com.example.abhilash.newsforyou.service;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhilash.newsforyou.API.News;
import com.example.abhilash.newsforyou.API.Value;
import com.example.abhilash.newsforyou.R;
import com.example.abhilash.newsforyou.data.NewsColumns;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Abhilash on 6/17/2016.
 */

public class SearchNewsAdapter extends RecyclerView.Adapter<SearchNewsAdapter.ViewHolder> {

    private News news;

    Context context;
    View mEmptyViewText;
    View mEmptyViewImage;

    public SearchNewsAdapter(Context context,News news,View emptyViewText,View emptyViewImage) {
        this.news = news;
        this.context = context;
        mEmptyViewText = emptyViewText;
        mEmptyViewImage =   emptyViewImage;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {

        Value value = news.getValue().get(position);
        holder.name.setText(value.getName());
        holder.provider.setText(value.getProvider().get(0).getName());

        if(value.getImage() != null)
        Picasso.with(context).load(value.getImage().getThumbnail().getContentUrl()).placeholder(R.mipmap.ic_empty).into(holder.newsImage);
        holder.newsImage.setContentDescription(value.getName());
        String publishedDate = value.getDatePublished();
        Calendar c = Calendar.getInstance();

        Date er = c.getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy",Locale.UK);

        SimpleDateFormat format = null;
        String sd = null;
        if(publishedDate.contains("T")){
             sd =publishedDate.substring(8,10)+" "+publishedDate.substring(5,7)+" " + publishedDate.substring(0,4)
                    +" "+ publishedDate.substring(11,19);
            format = new SimpleDateFormat("dd MM yyyy hh:mm:ss", Locale.UK);
        }else {

            sd =publishedDate.substring(8,10)+" "+publishedDate.substring(5,7)+" " + publishedDate.substring(0,4);
           format = new SimpleDateFormat("dd MM yyyy", Locale.UK);
        }

        Date newDate = null;
        try {
            newDate = format.parse(sd);

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        String timeAgo = null;
        long different =er.getTime() - newDate.getTime() ;

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

    @Override public int getItemCount() {
        int count = (news == null ? 0 : news.getValue().size());
        mEmptyViewText.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        mEmptyViewImage.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        return count;


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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


    }
}