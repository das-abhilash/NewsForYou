package com.example.abhilash.newsforyou.UI;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.abhilash.newsforyou.API.Image;
import com.example.abhilash.newsforyou.API.News;
import com.example.abhilash.newsforyou.API.Provider;
import com.example.abhilash.newsforyou.API.Value;
import com.example.abhilash.newsforyou.R;
import com.example.abhilash.newsforyou.data.NewsColumns;
import com.example.abhilash.newsforyou.data.NewsProvider;

import java.util.ArrayList;

public class WebActivity extends AppCompatActivity {
    String url;
    private boolean full = false;
    MenuItem mFavMenu;
    int ID;
    String DATE, NAME, DESC,URL,PROVIDER;
    Cursor cursor;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_main2);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);


        Intent i = getIntent();
        Bundle b = getIntent().getExtras();
        if (b.getString("tag").equals("search")){

            Value vv = (Value) i.getParcelableExtra("ValueExtra");
            DATE = vv.getDatePublished();
            NAME=vv.getName();
            DESC =vv.getDescription();
            url = vv.getUrl();
            Provider pp = (Provider) i.getParcelableExtra("ProviderExtra");
            PROVIDER = pp.getName();
                    URL = url;
            getSupportActionBar().setTitle("Search");
        }else{
            ID = b.getInt("ID");
            url = b.getString("URL");
            String title = b.getString("Title");

            cursor = this.getContentResolver().query(NewsProvider.News.CONTENT_URI, null, NewsColumns._ID + "=?",
                    new String[]{String.valueOf(ID)}, null);
            cursor.moveToFirst();
            getSupportActionBar().setTitle(title);

            URL = url;
            NAME = cursor.getString(cursor.getColumnIndex(NewsColumns.NAME));
            DATE = cursor.getString(cursor.getColumnIndex(NewsColumns.TIME));
            DESC = cursor.getString(cursor.getColumnIndex(NewsColumns.DESCRIPTION));
            PROVIDER = cursor.getString(cursor.getColumnIndex(NewsColumns.PROVIDER));

        }
        Cursor c = this.getContentResolver().query(NewsProvider.News.CONTENT_URI, null,
                NewsColumns.NAME + "=? AND " + NewsColumns.IS_FAV + "=? AND " + NewsColumns.TIME + "=?",
                new String[]{NAME, "true", DATE}, null);
        int count = c.getCount();
        if (count != 0) full = true;

        final WebView wb = (WebView) findViewById(R.id.webView1);


        wb.getSettings().setJavaScriptEnabled(true);


        wb.getSettings().setSupportZoom(true);

        if (wb != null) {
            wb.setWebViewClient(new NewsWebViewClient());
        }
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        wb.setWebChromeClient(new WebChromeClient() {
                                  public void onProgressChanged(WebView view, int progress) {
                                      if (progress < 100) {
                                          swipeRefreshLayout.setRefreshing(true);
                                      }

                                      if (progress == 100) {
                                          swipeRefreshLayout.setRefreshing(false);
                                      }
                                  }
                              }
        );
       /* AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Summery");

        // Setting Dialog Message
        alertDialog.setMessage(DESC);

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.tick);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();*/
        wb.loadUrl(url);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                    @Override
                                                    public void onRefresh() {

                                                        //works for only Get type
                                                        wb.reload();
                                                        //worls for both Get and Post type
                                                        //  wb.loadUrl(url);
                                                    }
                                                }
        );
    }

    private class NewsWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.web, menu);
        mFavMenu = menu.findItem(R.id.action_fav);
        if (!full)
            mFavMenu.setIcon(R.drawable.ic_favorite_border_white_36dp);
        else
            mFavMenu.setIcon(R.drawable.ic_favorite_white_36dp);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, NAME);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, url);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
            return true;
        }
        if (id == R.id.action_browser) {
            Intent sharingIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(sharingIntent);
            return true;
        }
       /* Drawable mDrawable = this.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY));*/
        //mDrawable.setColorFilter(Color.argb(255, 255, 255, 255));
        if (id == R.id.action_fav) {
            ArrayList<ContentProviderOperation> news = new ArrayList<ContentProviderOperation>();
            full = !full;
            if (full) {
                mFavMenu.setIcon(R.drawable.ic_favorite_white_36dp);
                ContentValues values = new ContentValues();

                values.put(NewsColumns.NAME,NAME );
                values.put(NewsColumns.URL, URL);
                values.put(NewsColumns.DESCRIPTION, DESC);
                values.put(NewsColumns.PROVIDER,PROVIDER );
                values.put(NewsColumns.TIME, DATE);
                values.put(NewsColumns.IS_FAV, "true");


                news.add(ContentProviderOperation.newInsert(NewsProvider.News.CONTENT_URI).withValues(values).build());


                Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();

            } else {
                news.add(ContentProviderOperation.newDelete(NewsProvider.News.CONTENT_URI).
                        withSelection(NewsColumns.NAME + "=? AND " + NewsColumns.IS_FAV + "=? AND " + NewsColumns.TIME + "=?",
                                new String[]{NAME, "true", DATE}).build());

                Toast.makeText(this, "Removed from Favorite", Toast.LENGTH_SHORT).show();
                mFavMenu.setIcon(R.drawable.ic_favorite_border_white_36dp);
            }

            try {
                this.getContentResolver().
                        applyBatch(NewsProvider.AUTHORITY, news);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }


            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
