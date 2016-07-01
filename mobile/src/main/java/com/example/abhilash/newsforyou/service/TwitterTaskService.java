package com.example.abhilash.newsforyou.service;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.abhilash.newsforyou.API.API;
import com.example.abhilash.newsforyou.API.BingNewsAPI;
import com.example.abhilash.newsforyou.API.Image;
import com.example.abhilash.newsforyou.API.News;
import com.example.abhilash.newsforyou.API.Value;
import com.example.abhilash.newsforyou.R;
import com.example.abhilash.newsforyou.UI.SearchActivity;
import com.example.abhilash.newsforyou.UI.WebActivity;
import com.example.abhilash.newsforyou.data.NewsColumns;
import com.example.abhilash.newsforyou.data.NewsProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Abhilash on 5/24/2016.
 */

public class TwitterTaskService extends GcmTaskService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static String text;
    public static final String ACTION_DATA_UPDATED =
            "com.example.abhilash.newsforyou.service.ACTION_DATA_UPDATED";

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NEWS_STATUS_OK, NEWS_API_ACCESS_DENIED,NEWS_API_VOLUME, NEWS_NO_SEARCH, NEWS_SEARCH_FAILED_CONNECT_SERVER,NEWS_NO_ADD,NEWS_ADD_FAILED_CONNECT_SERVER,NEWS_API_RATE})
    public @interface NewsStatus {
    }

    public static final int NEWS_STATUS_OK = 0;
    public static final int NEWS_API_ACCESS_DENIED = 1;
    public static final int NEWS_NO_SEARCH = 3;
    public static final int NEWS_SEARCH_FAILED_CONNECT_SERVER = 4;
    public static final int NEWS_NO_ADD = 5;
    public static final int NEWS_ADD_FAILED_CONNECT_SERVER = 6;
    public static final int NEWS_API_VOLUME = 7;
    public static final int NEWS_API_RATE = 8;

    News news;
    private Context mContext;
    private BingNewsAPI service;
    String SerachQuery;
    private GoogleApiClient mGoogleApiClient;
    public TwitterTaskService() {
        super();
    }

    public TwitterTaskService(Context context) {
        mContext = context;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public int onRunTask(TaskParams params) {
        if (mContext == null) {
            mContext = this;
        }

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        SerachQuery = params.getExtras().getString("SerachQuery");
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Ocp-Apim-Subscription-Key", API.API_KEY )
                        .build();
                return chain.proceed(newRequest);
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        OkHttpClient client = builder.build();

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl("https://api.cognitive.microsoft.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        service = restAdapter.create(BingNewsAPI.class);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String market = prefs.getString("country","en-IN");
        if (params.getExtras().getString ("tag").equals("search")){

            String count =  prefs.getString("search_count","20");
            Call<News> call = service.getBingSearch(SerachQuery, market,count );
             {
                call.enqueue(new retrofit2.Callback<News>() {

                    @Override
                    public void onResponse(Call<News> call, Response<News> response) {

                        news = response.body();

                        if (news == null){

                           int code= response.raw().code();
                            if (code == 401)
                            setNewsStatus(mContext, NEWS_API_ACCESS_DENIED);

                            else if (code == 403)
                            setNewsStatus(mContext, NEWS_API_VOLUME);
                            else if (code == 429)
                                setNewsStatus(mContext,  NEWS_API_RATE);
                            else
                            setNewsStatus(mContext, NEWS_NO_SEARCH);

                        }
                        else {
                            SearchActivity.setAdapter(news);
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("com.example.abhilash.newsforyou.service.setAdapter");

                            broadcastIntent.putExtra("NewsExtra", news);
                            mContext.sendBroadcast(broadcastIntent);
                            setNewsStatus(mContext, NEWS_STATUS_OK);
                        }
                    }

                    @Override
                    public void onFailure(Call<News> call, Throwable t) {
                        setNewsStatus(mContext, NEWS_SEARCH_FAILED_CONNECT_SERVER);
                    }
                });
            }

        } else if (params.getExtras().getString ("tag").equals("add")) {


            Call<News> call = service.getBingNews(SerachQuery, "en-IN", "20");
            {

                call.enqueue(new retrofit2.Callback<News>() {

                    @Override
                    public void onResponse(Call<News> call, Response<News> response) {
                        news = response.body();
                        if (news == null){

                            int code= response.raw().code();
                            if (code == 401)
                                setNewsStatus(mContext, NEWS_API_ACCESS_DENIED);

                            else if (code == 403)
                                setNewsStatus(mContext, NEWS_API_VOLUME);
                            else if (code == 429)
                                setNewsStatus(mContext,  NEWS_API_RATE);
                            else
                                setNewsStatus(mContext, NEWS_NO_ADD);
                        }
                        else {
                            List<Value> value = news.getValue();

                            int size = value.size();

                            ArrayList<ContentProviderOperation> news = new ArrayList<ContentProviderOperation>();
                            news.add(ContentProviderOperation.newDelete(NewsProvider.News.CONTENT_URI).withSelection(NewsColumns.CATEGORY + "=? AND " + NewsColumns.IS_FAV + "=?", new String[]{SerachQuery, "false"}).build());
                            for (int i = 0; i < size; i++) {
                                ContentValues values = new ContentValues();

                                values.put(NewsColumns.NAME, String.valueOf(value.get(i).getName()));
                                values.put(NewsColumns.URL, String.valueOf(value.get(i).getUrl()));
                                values.put(NewsColumns.DESCRIPTION, value.get(i).getDescription());
                                values.put(NewsColumns.PROVIDER, String.valueOf(value.get(i).getProvider().get(0).getName()));
                                values.put(NewsColumns.TIME, String.valueOf(value.get(i).getDatePublished()));
                                values.put(NewsColumns.IS_FAV, "false");

                                Image de = value.get(i).getImage();
                                if (de != null)
                                    values.put(NewsColumns.IMAGE, de.getThumbnail().getContentUrl());
                                values.put(NewsColumns.CATEGORY, SerachQuery);

                                news.add(ContentProviderOperation.newInsert(NewsProvider.News.CONTENT_URI).withValues(values).build());
                            }
                            try {
                                mContext.getContentResolver().
                                        applyBatch(NewsProvider.AUTHORITY, news);
                                updateWidgets();
                                if (SerachQuery.equals(""))
                                updateWearApp();
                                notifyNews();
                            } catch (RemoteException | OperationApplicationException e) {

                                e.printStackTrace();

                            }


                            setNewsStatus(mContext, NEWS_STATUS_OK);
                        }
                    }

                    @Override
                    public void onFailure(Call<News> call, Throwable t) {
                        setNewsStatus(mContext, NEWS_ADD_FAILED_CONNECT_SERVER);

                    }
                });
            }

        }

        updateWidgets();
        return GcmNetworkManager.RESULT_SUCCESS;
    }
    private void notifyNews() {

        //checking the last update and notify if it' the first of the day
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String displayNotificationsKey = "notification";
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey, true);

        if (displayNotifications) {

            Intent intent = new Intent(getApplicationContext(), NotificationIntentService.class);
            startService(intent);
        }
    }
    private void updateWearApp() {
        if((mGoogleApiClient == null) || !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        Cursor data = mContext.getContentResolver().query(NewsProvider.News.CONTENT_URI, null,
                NewsColumns.CATEGORY + "=? AND " + NewsColumns.IS_FAV + "=?"
                , new String[]{"","false"}, null );
        if (data != null) {
            data.moveToFirst();
        }
        String newsName = null + "`";

        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {

            newsName += data.getString(data.getColumnIndex(NewsColumns.NAME)) + "`";

        }

        if (data != null) {
            data.close();
        }
        PutDataMapRequest dataMap = PutDataMapRequest.create ("/news_name");
        dataMap.getDataMap().putString("key",newsName /*+ System.currentTimeMillis()*/ );

        PutDataRequest request = dataMap.asPutDataRequest();
        request.setUrgent();


        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {

                    }
                });
    }

        public void updateWidgets() {
        Context context = mContext;

        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }

    static private void setNewsStatus(Context c, @NewsStatus int NewsStatus) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_news_status_key), NewsStatus);
        spe.commit();
    }

}