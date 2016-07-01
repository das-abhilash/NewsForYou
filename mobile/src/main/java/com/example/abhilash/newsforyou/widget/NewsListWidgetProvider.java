package com.example.abhilash.newsforyou.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.example.abhilash.newsforyou.R;
import com.example.abhilash.newsforyou.UI.CategoryActivity;
import com.example.abhilash.newsforyou.UI.MainActivity;
import com.example.abhilash.newsforyou.UI.WebActivity;
import com.example.abhilash.newsforyou.service.TwitterTaskService;

/**
 * Created by Abhilash on 6/20/2016.
 */
public class NewsListWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {


        {
            for (int appWidgetId : appWidgetIds) {

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_collection);

                // Create an Intent to launch MainActivity
                Intent intent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0/*PendingIntent.FLAG_CANCEL_CURRENT*/);
                views.setOnClickPendingIntent(R.id.widget, pendingIntent);

                // Set up the collection
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    setRemoteAdapter(context, views,appWidgetId);
                } else {
                    setRemoteAdapterV11(context, views,appWidgetId);
                }

                PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                        .addNextIntentWithParentStack(new Intent(context, WebActivity.class))
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
                views.setEmptyView(R.id.widget_list, R.id.widget_empty);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }




    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        if (TwitterTaskService.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        }
    }

    /* @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
        private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_icon, description);

           }*/

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views, int appWidgetId) {
        Intent intent = new Intent(context, NewsWidgetRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        views.setRemoteAdapter(R.id.widget_list, intent);
    }

    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views, int appWidgetId) {
        Intent intent = new Intent(context, NewsWidgetRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        views.setRemoteAdapter(0, R.id.widget_list,intent
                /*new Intent(context, NewsWidgetRemoteViewsService.class)*/);
    }



}
