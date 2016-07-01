package com.example.abhilash.newsforyou.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhilash.newsforyou.API.Image;
import com.example.abhilash.newsforyou.R;
import com.example.abhilash.newsforyou.data.NewsColumns;
import com.example.abhilash.newsforyou.data.NewsProvider;
import com.example.abhilash.newsforyou.service.NewsAdapter;
import com.example.abhilash.newsforyou.service.NewsIntentService;
import com.example.abhilash.newsforyou.service.RecyclerItemClickListener;
import com.example.abhilash.newsforyou.service.TwitterTaskService;
import com.example.abhilash.newsforyou.service.Utility;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CategoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Boolean mParam3;

    private OnFragmentInteractionListener mListener;

    public CategoryFragment() {
        // Required empty public constructor
    }


    public static CategoryFragment newInstance(String param1,String param2, Boolean param3) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putBoolean(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    RecyclerView recyclerView;
    private Cursor mCursor;
    NewsAdapter twitterAdapter;
    private static final int CURSOR_LOADER_ID = 0;
    TextView emptyText;
    ImageView emptyImage;



    Intent mServiceIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getBoolean(ARG_PARAM3);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_category, container, false);
        mServiceIntent = new Intent(getActivity(), NewsIntentService.class);
        mServiceIntent.putExtra("SerachQuery", mParam1);
        mServiceIntent.putExtra("tag", "add");
        getActivity().startService(mServiceIntent);
        if (mParam3 == true ) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mParam2);
        }


        emptyImage = (ImageView) getActivity().findViewById(R.id.recycler_view_empty_icon);
        emptyText = (TextView) getActivity().findViewById(R.id.recycler_view_empty_text);

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        mCursor.moveToPosition(position);
                        String url = mCursor.getString(mCursor.getColumnIndex(NewsColumns.URL));
                        int ID = mCursor.getInt(mCursor.getColumnIndex(NewsColumns._ID));
                        Intent webIntent = new Intent(view.getContext(), WebActivity.class);
                        webIntent.putExtra("URL", url);
                        webIntent.putExtra("ID", ID);
                        webIntent.putExtra("tag", "add");
                        webIntent.putExtra("Title", mParam2);
                        startActivity(webIntent);
                        getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.zoom_out);
                    }
                })
        );


        return recyclerView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(), NewsProvider.News.CONTENT_URI, null,
                NewsColumns.CATEGORY + "=? AND " + NewsColumns.IS_FAV + "=?"
                , new String[]{mParam1,"false"}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


            twitterAdapter = new NewsAdapter(getContext(), data,emptyText,emptyImage);
            mCursor = data;
            recyclerView.setAdapter(twitterAdapter);
            updateEmptyView();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void updateEmptyView() {

        if (twitterAdapter.getItemCount() == 0) {
            TextView ev = (TextView) getActivity().findViewById(R.id.recycler_view_empty_text);
            if (null != ev) {

                int message;
                @TwitterTaskService.NewsStatus int NewsStatus = getLocationStatus(getActivity());

                switch (NewsStatus) {
                    case TwitterTaskService.NEWS_API_ACCESS_DENIED:
                        message = R.string.empty_api_access_denied;
                        break;
                    case TwitterTaskService.NEWS_NO_ADD:
                        message = R.string.empty_no_add;
                        break;
                    case TwitterTaskService.NEWS_ADD_FAILED_CONNECT_SERVER:
                        message = R.string.empty_failed_connect_server;
                        break;
                    case TwitterTaskService.NEWS_API_VOLUME:
                        message = R.string.empty_api_volume;
                        break;
                    default:
                        message = R.string.empty_list;

                }
                ev.setText(message);
            }

        }
    }

    @SuppressWarnings("ResourceType")
    static public
    @TwitterTaskService.NewsStatus
    int getLocationStatus(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_news_status_key), TwitterTaskService.NEWS_ADD_FAILED_CONNECT_SERVER);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getActivity().getString(R.string.pref_news_status_key)))
        {
            updateEmptyView();
        } else if (key.equals(getActivity().getString(R.string.pref_country_key))){
            mParam1 = Utility.getCountry(getActivity());
                    }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
