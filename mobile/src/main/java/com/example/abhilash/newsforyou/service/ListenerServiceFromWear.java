package com.example.abhilash.newsforyou.service;

import android.content.Intent;
import android.database.Cursor;

import com.example.abhilash.newsforyou.UI.WebActivity;
import com.example.abhilash.newsforyou.data.NewsColumns;
import com.example.abhilash.newsforyou.data.NewsProvider;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Abhilash on 6/30/2016.
 */
public class ListenerServiceFromWear extends WearableListenerService {

    private static final String BROWSE_PATH = "/browse";


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        String newsData = messageEvent.getPath();
        String[] msg = newsData.split("`");
        if (msg[0].equals(BROWSE_PATH)) {

            Cursor data = getApplicationContext().getContentResolver().query(
            NewsProvider.News.CONTENT_URI, null,
                    NewsColumns.NAME + "=? "
                    , new String[]{msg[1]}, null);

            Intent startIntent = new Intent(getApplicationContext(), WebActivity.class);

            data.moveToFirst();
            if (data != null) {
                String url = data.getString(data.getColumnIndex(NewsColumns.URL));
                int ID = data.getInt(data.getColumnIndex(NewsColumns._ID));


                startIntent.putExtra("URL", url);
                startIntent.putExtra("ID", ID);
                startIntent.putExtra("tag", "add");
                startIntent.putExtra("Title", "Top Stories");
                startIntent.putExtra("SearchQuery", "");
                startIntent.putExtra("Title", "Top Stories");
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startIntent);
            }
        }

    }
}
