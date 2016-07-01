package com.example.abhilash.newsforyou.UI;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.abhilash.newsforyou.R;

public class CategoryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    String SearchQuery,Title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        SearchQuery = b.getString("SearchQuery");
        Title = b.getString("Title");
        CategoryFragment categoryFragment = new CategoryFragment();
        categoryFragment.setArguments(b);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.category_fragment, categoryFragment.newInstance(SearchQuery, Title,true))
                .commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent categoryIntent = new Intent(this, MainActivity.class);
            categoryIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP /*| Intent.FLAG_ACTIVITY_CLEAR_TASK */ /*CLEAR_WHEN_TASK_RESET*/);

            startActivity(categoryIntent);
        } else if (id == R.id.nav_topStories) {

            CategoryFragment categoryFragment = new CategoryFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.category_fragment, categoryFragment.newInstance("", "Top Stories",true))
                    .commit();

        } else if (id == R.id.nav_fav) {
            Intent intent = new Intent(this, FavActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }  else if (id == R.id.nav_country) {
            CategoryFragment categoryFragment = new CategoryFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.category_fragment, categoryFragment.newInstance("India", "India",true))
                    .commit();

        } else if (id == R.id.nav_entertainment) {
            CategoryFragment categoryFragment = new CategoryFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.category_fragment, categoryFragment.newInstance("Entertainment", "Entertainment",true))
                    .commit();

        }else if (id == R.id.nav_sat) {

            CategoryFragment categoryFragment = new CategoryFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.category_fragment, categoryFragment.newInstance("ScienceAndTechnology", "Science And Technology",true))
                    .commit();
        } else if (id == R.id.nav_business) {

            CategoryFragment categoryFragment = new CategoryFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.category_fragment, categoryFragment.newInstance("Business", "Business",true))
                    .commit();

        } else if (id == R.id.nav_politics) {

            CategoryFragment categoryFragment = new CategoryFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.category_fragment, categoryFragment.newInstance("Politics", "Politics",true))
                    .commit();

        }  else if (id == R.id.nav_sports) {

            CategoryFragment categoryFragment = new CategoryFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.category_fragment, categoryFragment.newInstance("Sports", "Sports",true))
                    .commit();

        } else if (id == R.id.nav_lifestyle) {
            CategoryFragment categoryFragment = new CategoryFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.category_fragment, categoryFragment.newInstance("Lifestyle", "Lifestyle",true))
                    .setCustomAnimations(R.anim.pull_in_right, R.anim.zoom_out)
                    .commit();

        }else if (id == R.id.nav_rate){
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + this.getPackageName())));
            } catch (android.content.ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
