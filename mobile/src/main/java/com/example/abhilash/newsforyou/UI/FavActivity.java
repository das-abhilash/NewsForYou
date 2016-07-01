package com.example.abhilash.newsforyou.UI;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abhilash.newsforyou.R;
import com.example.abhilash.newsforyou.data.NewsColumns;
import com.example.abhilash.newsforyou.data.NewsProvider;
import com.example.abhilash.newsforyou.service.NewsAdapter;
import com.example.abhilash.newsforyou.service.RecyclerItemClickListener;

public class FavActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    RecyclerView recyclerView;
    private Cursor mCursor;
    SwipeRefreshLayout swipeRefreshLayout;
    NewsAdapter twitterAdapter;
    private static final int CURSOR_LOADER_ID = 0;

    TextView emptyText;
    ImageView emptyImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.fav_recycler_view);
        emptyImage = (ImageView) findViewById(R.id.recycler_view_empty_icon);
        emptyText = (TextView) findViewById(R.id.recycler_view_empty_text);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        mCursor.moveToPosition(position);
                        String url = mCursor.getString(mCursor.getColumnIndex(NewsColumns.URL));
                        int ID = mCursor.getInt(mCursor.getColumnIndex(NewsColumns._ID));
                        Intent webIntent = new Intent(view.getContext(), WebActivity.class);
                        webIntent.putExtra("URL", url);
                        webIntent.putExtra("ID", ID);
                        startActivity(webIntent);
                    }
                })
        );


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                    @Override
                                                    public void onRefresh() {

                                                        RE();
                                                    }
                                                }
        );


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void RE() {
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this, NewsProvider.News.CONTENT_URI, null,
                NewsColumns.IS_FAV + "=?"
                , new String[]{"true"}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        swipeRefreshLayout.setRefreshing(false);
        if (data != null) {

            twitterAdapter = new NewsAdapter(this, data,emptyText,emptyImage);
            mCursor = data;
            recyclerView.setAdapter(twitterAdapter);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
