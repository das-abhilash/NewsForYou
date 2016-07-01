package com.example.abhilash.newsforyou.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.abhilash.newsforyou.R;
import com.example.abhilash.newsforyou.UI.MainActivity;
import com.example.abhilash.newsforyou.UI.WebActivity;
import com.example.abhilash.newsforyou.data.NewsColumns;
import com.example.abhilash.newsforyou.data.NewsProvider;
import com.google.android.gms.gcm.TaskParams;

import java.util.concurrent.ExecutionException;

/**
 * Created by Abhilash on 7/1/2016.
 */
public class NotificationIntentService extends IntentService {
    public NotificationIntentService(String name) {
        super(name);
    }


    public NotificationIntentService() {
        super(NotificationIntentService.class.getName());

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onHandleIntent(Intent intent) {

        Cursor cursor = getApplicationContext().getContentResolver().query(NewsProvider.News.CONTENT_URI, null,
                NewsColumns.CATEGORY + "=? AND " + NewsColumns.IS_FAV + "=?"
                , new String[]{"", "false"}, null);
        cursor.moveToFirst();
        Resources resources = getApplicationContext().getResources();
        @SuppressLint("InlinedApi")
        int largeIconWidth = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                ? resources.getDimensionPixelSize(android.R.dimen.notification_large_icon_width)
                : resources.getDimensionPixelSize(R.dimen.notification_large_icon_default);
        @SuppressLint("InlinedApi")
        int largeIconHeight = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                ? resources.getDimensionPixelSize(android.R.dimen.notification_large_icon_height)
                : resources.getDimensionPixelSize(R.dimen.notification_large_icon_default);
        Bitmap largeIcon;
        try {
            largeIcon = Glide.with(getApplicationContext())
                    .load(cursor.getString(cursor.getColumnIndex(NewsColumns.IMAGE)))
                    .asBitmap()
                    .error(R.mipmap.ic_launcher)
                    .fitCenter()
                    .into(largeIconWidth, largeIconHeight).get();
        } catch (InterruptedException | ExecutionException e) {
            largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
        } //BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher);
        int id = 1;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("News Alert")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(cursor.getString(cursor.getColumnIndex(NewsColumns.NAME))))
                        .setContentText(cursor.getString(cursor.getColumnIndex(NewsColumns.NAME)));
        Intent resultIntent = new Intent(this, MainActivity.class);
        /*String title;
        String categroy = cursor.getString(cursor.getColumnIndex(NewsColumns.CATEGORY));
        if (categroy.equals(""))
            title = "Top Stories";
        else if (categroy.equals("ScienceAndTechnology"))
            title = "Science And Technology";
        else title = categroy;
        resultIntent.putExtra("URL", cursor.getString(cursor.getColumnIndex(NewsColumns.URL)));
        resultIntent.putExtra("ID", cursor.getString(cursor.getColumnIndex(NewsColumns._ID)));
        resultIntent.putExtra("tag", "add");
        resultIntent.putExtra("Title", title);*/

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(WebActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, mBuilder.build());


        scheduleNextUpdate();
    }

    private void scheduleNextUpdate() {
        Intent intent = new Intent(this, this.getClass());
        PendingIntent pendingIntent =
                PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // The update frequency should often be user configurable.  This is not.

        long currentTimeMillis = System.currentTimeMillis();
        long nextUpdateTimeMillis = currentTimeMillis + 15 * DateUtils.MINUTE_IN_MILLIS;
        Time nextUpdateTime = new Time();
        nextUpdateTime.set(nextUpdateTimeMillis);

        if (nextUpdateTime.hour < 8 || nextUpdateTime.hour >= 18) {
            nextUpdateTime.hour = 8;
            nextUpdateTime.minute = 0;
            nextUpdateTime.second = 0;
            nextUpdateTimeMillis = nextUpdateTime.toMillis(false) + DateUtils.DAY_IN_MILLIS;
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, nextUpdateTimeMillis, pendingIntent);
    }

}
