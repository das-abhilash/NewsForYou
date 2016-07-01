package com.example.abhilash.newsforyou.UI;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.abhilash.newsforyou.R;
import com.example.abhilash.newsforyou.data.NewsColumns;
import com.example.abhilash.newsforyou.service.NewsIntentService;
import com.example.abhilash.newsforyou.service.NotificationIntentService;
import com.example.abhilash.newsforyou.service.TwitterTaskService;
import com.example.abhilash.newsforyou.service.Utility;
import com.example.abhilash.newsforyou.service.ViewPagerAdapter;
import com.example.abhilash.newsforyou.data.NewsProvider;
import com.example.abhilash.newsforyou.service.NewsAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    boolean doubleBackToExitPressedOnce = false;
    ViewPager pager;
    Intent intent;
    TabLayout tabLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    private GoogleApiClient mGoogleApiClient;
    long period = 60000L;
    long flex = 100L;
    String periodicTag = "periodic";
    private static final float MIN_SCALE_DEPTH = 0.75f;
    private static final float MIN_SCALE_ZOOM = 0.85f;
    private static final float MIN_ALPHA_ZOOM = 0.5f;
    private static final float SCALE_FACTOR_SLIDE = 0.85f;
    private static final float MIN_ALPHA_SLIDE = 0.35f;

    String[] categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categories = new String[]{"", Utility.getCountry(this), "World", "Entertainment", "ScienceAndTechnology", "Business", "Politics", "Sports", "Lifestyle"};

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (isNetworkAvailable()) {
            for (int i = 0; i < categories.length; i++) {
                Bundle bundle = new Bundle();
                bundle.putString("SerachQuery", categories[i]);
                bundle.putString("tag", "add");
                PeriodicTask periodicTask = new PeriodicTask.Builder()
                        .setService(TwitterTaskService.class)
                        .setPeriod(period)
                        .setFlex(flex)
                        .setTag(periodicTag)
                        .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                        .setRequiresCharging(false)
                        .setExtras(bundle)
                        .build();

                GcmNetworkManager.getInstance(this).schedule(periodicTask);
            }
        } else
            showAlert();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_main);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setEnabled(false);

        pager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        FragmentManager manager = getSupportFragmentManager();

        ViewPagerAdapter adapter = new ViewPagerAdapter(manager,this);
        pager.setAdapter(adapter);

        pager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {

                final float alpha;
                final float scale;
                final float translationX;

                if (position >= -1 && position <= 1) {
                    scale = Math.max(MIN_SCALE_ZOOM, 1 - Math.abs(position));
                    alpha = MIN_ALPHA_ZOOM +
                            (scale - MIN_SCALE_ZOOM) / (1 - MIN_SCALE_ZOOM) * (1 - MIN_ALPHA_ZOOM);
                    float vMargin = page.getHeight() * (1 - scale) / 2;
                    float hMargin = page.getWidth() * (1 - scale) / 2;
                    if (position < 0) {
                        translationX = (hMargin - vMargin / 2);
                    } else {
                        translationX = (-hMargin + vMargin / 2);
                    }
                } else {
                    alpha = 1;
                    scale = 1;
                    translationX = 0;
                }

                page.setAlpha(alpha);
                page.setTranslationX(translationX);
                page.setScaleX(scale);
                page.setScaleY(scale);

            }
        });
        tabLayout.setupWithViewPager(pager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                    startActivity(intent);
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void showAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);


        alertDialog.setTitle("No Internet connection.");

        alertDialog.setMessage("You have no internet connection. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Snackbar.make(getCurrentFocus(), "This is OLD Data. Connect to the Internet to Refresh it", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
                dialog.cancel();
            }
        });

        alertDialog.show();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce)
                super.onBackPressed();
        }


        this.doubleBackToExitPressedOnce = true;


        Snackbar.make(getCurrentFocus(), "Please click BACK again to exit", Snackbar.LENGTH_SHORT)
                .setAction("Action", null)
                .show();


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1500);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mGoogleApiClient.connect();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        if ((mGoogleApiClient != null) && (mGoogleApiClient.isConnected())) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    private void updateWearApp() {

        Cursor data = getContentResolver().query(NewsProvider.News.CONTENT_URI, null,
                NewsColumns.CATEGORY + "=? AND " + NewsColumns.IS_FAV + "=?"
                , new String[]{"", "false"}, null);
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
        PutDataMapRequest dataMap = PutDataMapRequest.create("/news_name");
        dataMap.getDataMap().putString("key", newsName /*+ System.currentTimeMillis()*/);

        PutDataRequest request = dataMap.asPutDataRequest();
        request.setUrgent();


        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }
        if (id == R.id.action_refresh) {
            swipeRefreshLayout.setRefreshing(true);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 5000);

            for (int i = 0; i < categories.length; i++) {
                intent = new Intent(this, NewsIntentService.class);
                intent.putExtra("SerachQuery", categories[i]);
                intent.putExtra("tag", "add");
                startService(intent);

            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent categoryIntent = new Intent(this, MainActivity.class);
            startActivity(categoryIntent);
        } else if (id == R.id.nav_topStories) {
            Intent categoryIntent = new Intent(this, CategoryActivity.class);
            categoryIntent.putExtra("SearchQuery", "");
            categoryIntent.putExtra("Title", "Top Stories");
            startActivity(categoryIntent);

        } else if (id == R.id.nav_fav) {
            Intent intent = new Intent(this, FavActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_country) {
            Intent categoryIntent = new Intent(this, CategoryActivity.class);
            categoryIntent.putExtra("SearchQuery", "India");
            categoryIntent.putExtra("Title", "India");
            startActivity(categoryIntent);

        } else if (id == R.id.nav_entertainment) {
            Intent categoryIntent = new Intent(this, CategoryActivity.class);
            categoryIntent.putExtra("SearchQuery", "Entertainment");
            categoryIntent.putExtra("Title", "Entertainment");
            startActivity(categoryIntent);

        } else if (id == R.id.nav_sat) {
            Intent categoryIntent = new Intent(this, CategoryActivity.class);
            categoryIntent.putExtra("SearchQuery", "ScienceAndTechnology");
            categoryIntent.putExtra("Title", "Science And Technology");
            startActivity(categoryIntent);
        } else if (id == R.id.nav_business) {
            Intent categoryIntent = new Intent(this, CategoryActivity.class);
            categoryIntent.putExtra("SearchQuery", "Business");
            categoryIntent.putExtra("Title", "Business");
            startActivity(categoryIntent);

        } else if (id == R.id.nav_politics) {
            Intent categoryIntent = new Intent(this, CategoryActivity.class);
            categoryIntent.putExtra("SearchQuery", "Politics");
            categoryIntent.putExtra("Title", "Politics");
            startActivity(categoryIntent);

        } else if (id == R.id.nav_sports) {
            Intent categoryIntent = new Intent(this, CategoryActivity.class);
            categoryIntent.putExtra("SearchQuery", "Sports");
            categoryIntent.putExtra("Title", "Sports");
            startActivity(categoryIntent);

        } else if (id == R.id.nav_lifestyle) {
            Intent categoryIntent = new Intent(this, CategoryActivity.class);
            categoryIntent.putExtra("SearchQuery", "Lifestyle");
            categoryIntent.putExtra("Title", "Lifestyle");
            startActivity(categoryIntent);

        } else if (id == R.id.nav_rate) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + this.getPackageName())));
            } catch (android.content.ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
            }
        } else if (id == R.id.nav_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        else if (id == R.id.nav_feedback) {

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"help@example.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "FeedBack");

            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onConnected(Bundle bundle) {
        updateWearApp();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}
