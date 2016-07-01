package com.example.abhilash.newsforyou.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.example.abhilash.newsforyou.R;
import com.example.abhilash.newsforyou.data.NewsColumns;
import com.example.abhilash.newsforyou.data.NewsProvider;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Created by Abhilash on 6/20/2016.
 */
public class NewsWidgetRemoteViewsService extends RemoteViewsService {


    private static final String[] NEWS_COLUMNS = {
            NewsColumns._ID,
            NewsColumns.URL,
            NewsColumns.IMAGE,
            NewsColumns.NAME,
            NewsColumns.PROVIDER,
            NewsColumns.TIME
    };
    // these indices must match the projection
    private static final int INDEX_ID = 0;
    private static final int INDEX_URL = 1;
    private static final int INDEX_IMAGE = 2;
    private static final int INDEX_NAME = 3;
    private static final int INDEX_PROVIDER = 4;
    private static final int INDEX_TIME = 5;


    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(/*NewsProvider.News.CONTENT_URI, NEWS_COLUMNS, null,
                        null, null*/NewsProvider.News.CONTENT_URI, NEWS_COLUMNS,
                        NewsColumns.CATEGORY + "=? AND " + NewsColumns.IS_FAV + "=?"
                        , new String[]{"","false"}, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {

                int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,0);


                boolean large= true;
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }

                int widgetWidth = getWidgetWidth(AppWidgetManager.getInstance(getApplicationContext()), appWidgetId);
                int defaultWidth = getResources().getDimensionPixelSize(R.dimen.widget_news_default_width);
                //  int largeWidth = getResources().getDimensionPixelSize(R.dimen.widget_today_large_width);
                int layoutId = 0;
                if (widgetWidth >= defaultWidth) {
                    layoutId = R.layout.widget_detail_list_item;
                } else {
                    large = false;
                    layoutId = R.layout.widget_detail_list_item_small;
                }
                RemoteViews views = new RemoteViews(getPackageName(), layoutId);


                  /*RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);*/
               // final RemoteViews view = views;



                int id = data.getInt(INDEX_ID);
                String name = data.getString(INDEX_NAME);
                String url = data.getString(INDEX_URL);
                String image = data.getString(INDEX_IMAGE);
                String  provider = data.getString(INDEX_PROVIDER);
                String  time = data.getString(INDEX_TIME);

              //  views.setBitmap(R.id.newsImage,);
                views.setTextViewText(R.id.name,name);
                views.setTextViewText(R.id.provider, provider);
               // Picasso.with(getApplicationContext()).load(image).into(R.id.newsImage);

                views.setTextViewText(R.id.provider, provider);

                ///////////////////////
                if(large){
                    Bitmap newsImage = null;

                    try {
                        newsImage = Glide.with(NewsWidgetRemoteViewsService.this)
                                .load(image)
                                .asBitmap()
                                .placeholder(R.mipmap.ic_empty)
                                        // .error(weatherArtResourceId)
                                .into(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, com.bumptech.glide.request.target.Target.SIZE_ORIGINAL).get();
                    } catch (InterruptedException | ExecutionException ignored) {
                    }

                    if (newsImage != null) {
                        views.setImageViewBitmap(R.id.newsImage, newsImage);
                    }
                }

                views.setContentDescription(R.id.newsImage,name);

                String publishedDate = time;
                Calendar c = Calendar.getInstance();

                Date er = c.getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.UK);
                String formattedDate = df.format(c.getTime());

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


                views.setTextViewText(R.id.time, timeAgo);


               /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views,stock_symbol,isUp,change);
                }*/
                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra("ID", id);
                fillInIntent.putExtra("URL", url);
                fillInIntent.putExtra("Title", "Top Stories");
                fillInIntent.putExtra("tag", "add");

                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            /*@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views,String stock_symbol,int isUp,String change) {
                String upDescription = "down by";
                if (isUp ==1)upDescription = "up by" ;
                views.setContentDescription(R.id.widget_list_item, getString(R.string.a11y_widget,stock_symbol,change,upDescription));
            }*/

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
    int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
        // Prior to Jelly Bean, widgets were always their default size
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return  getResources().getDimensionPixelSize(R.dimen.widget_news_default_width);
        }
        // For Jelly Bean and higher devices, widgets can be resized - the current size can be
        // retrieved from the newly added App Widget Options
        return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            // The width returned is in dp, but we'll convert it to pixels to match the other widths
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
                    displayMetrics);
        }
        return  getResources().getDimensionPixelSize(R.dimen.widget_news_default_width);
    }
}
