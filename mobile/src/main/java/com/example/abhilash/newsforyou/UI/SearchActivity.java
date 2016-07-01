package com.example.abhilash.newsforyou.UI;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhilash.newsforyou.API.News;
import com.example.abhilash.newsforyou.API.Provider;
import com.example.abhilash.newsforyou.API.Value;
import com.example.abhilash.newsforyou.R;
import com.example.abhilash.newsforyou.data.NewsColumns;
import com.example.abhilash.newsforyou.data.NewsProvider;
import com.example.abhilash.newsforyou.service.NewsAdapter;
import com.example.abhilash.newsforyou.service.NewsIntentService;
import com.example.abhilash.newsforyou.service.RecyclerItemClickListener;
import com.example.abhilash.newsforyou.service.SearchNewsAdapter;
import com.example.abhilash.newsforyou.service.TwitterTaskService;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    Intent mServiceIntent;
    String query;
    static News news;
    TextView emptyText;
    ImageView emptyImage;
    SearchNewsAdapter searchNewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        emptyImage = (ImageView) findViewById(R.id.recycler_view_empty_icon);
        emptyText = (TextView) findViewById(R.id.recycler_view_empty_text);

        emptyText.setVisibility(View.GONE);
        emptyImage.setVisibility(View.GONE);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.abhilash.newsforyou.service.setAdapter");
        registerReceiver(Receiver, filter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        searchNewsAdapter = new SearchNewsAdapter(this, news, emptyText, emptyImage);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Value value = news.getValue().get(position);
                        String Name = value.getName();
                        String url = value.getUrl();
                        Provider provider = value.getProvider().get(0);
                        Intent webIntent = new Intent(getApplicationContext(), WebActivity.class);
                        webIntent.putExtra("ValueExtra", value);
                        webIntent.putExtra("ProviderExtra", provider);
                        webIntent.putExtra("tag", "search");
                        startActivity(webIntent);
                        // getApplicationContext().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                    }
                })
        );


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                    @Override
                                                    public void onRefresh() {
                                                        mServiceIntent = new Intent(getApplicationContext(), NewsIntentService.class);
                                                        mServiceIntent.putExtra("SerachQuery", query);
                                                        startService(mServiceIntent);
                                                        //   RE();
                                                    }
                                                }
        );

    }

    BroadcastReceiver Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            news = intent.getParcelableExtra("NewsExtra");
            searchNewsAdapter = new SearchNewsAdapter(context, news, emptyText, emptyImage);
            recyclerView.setAdapter(searchNewsAdapter);
            swipeRefreshLayout.setRefreshing(false);


            /*Value vvv = intent.getParcelableExtra("ValueExtra");

            Provider ppp = intent.getParcelableExtra("ProviderExtra");
            */

            updateEmptyView();

        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(Receiver);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            query = intent.getStringExtra(SearchManager.QUERY);
            mServiceIntent = new Intent(this, NewsIntentService.class);
            mServiceIntent.putExtra("tag", "search");
            mServiceIntent.putExtra("SerachQuery", query);
            startService(mServiceIntent);
            swipeRefreshLayout.setRefreshing(true);

            updateEmptyView();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Search any topic in Web");
        searchItem.expandActionView();
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName cn = new ComponentName(this, SearchActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
        //searchView.expandActionView();
        // searchView.setIconifiedByDefault(false);
        searchView.requestFocus();
       /* int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        if (searchPlate!=null) {
            searchPlate.setBackgroundColor(Color.DKGRAY);
            int searchTextId = searchPlate.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView searchText = (TextView) searchPlate.findViewById(searchTextId);
            if (searchText!=null) {
                searchText.setTextColor(Color.WHITE);
                searchText.setHintTextColor(Color.WHITE);
            }
        }*/

        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    public static void setAdapter(News neqws) {
        news = neqws;
    }

    private void updateEmptyView() {

        if (searchNewsAdapter.getItemCount() == 0) {
            if (null != emptyText) {

                int message;
                @TwitterTaskService.NewsStatus int NewsStatus = getLocationStatus(this);

                int i = 0;
                switch (NewsStatus) {
                    case TwitterTaskService.NEWS_API_ACCESS_DENIED:
                        message = R.string.empty_api_access_denied;
                        break;
                    case TwitterTaskService.NEWS_NO_SEARCH:
                        message = R.string.empty_no_Search;
                        break;
                    case TwitterTaskService.NEWS_SEARCH_FAILED_CONNECT_SERVER:
                        message = R.string.empty_failed_connect_server;
                        break;
                    case TwitterTaskService.NEWS_API_VOLUME:
                        message = R.string.empty_api_volume;
                        break;
                    default:
                        message = R.string.empty_list;

                }
                emptyText.setText(message);
                swipeRefreshLayout.setRefreshing(false);
            }

        }
    }

    @SuppressWarnings("ResourceType")
    static public
    @TwitterTaskService.NewsStatus
    int getLocationStatus(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_news_status_key), TwitterTaskService.NEWS_SEARCH_FAILED_CONNECT_SERVER);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_news_status_key))) {
            updateEmptyView();
        }
    }
}
