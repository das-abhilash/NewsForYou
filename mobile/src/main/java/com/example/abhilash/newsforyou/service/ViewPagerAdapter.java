package com.example.abhilash.newsforyou.service;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.abhilash.newsforyou.UI.CategoryFragment;
import com.example.abhilash.newsforyou.UI.MainActivity;

/**
 * Created by Abhilash on 6/10/2016.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    Context context;
    public ViewPagerAdapter(FragmentManager fm, Context c) {
        super(fm);
        context = c;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag=null;
        String title = (String) getPageTitle(position);
        switch (position){
            case 0:
              return   CategoryFragment.newInstance("",title,false);
            case 1:
                return  CategoryFragment.newInstance(Utility.getCountry(context),title,false);
            case 2:
                return  CategoryFragment.newInstance("World",title,false);
            case 3:
                return   CategoryFragment.newInstance("Entertainment",title,false);
            case 4:
                return   CategoryFragment.newInstance("ScienceAndTechnology",title,false);
            case 5:
                return   CategoryFragment.newInstance("Business",title,false);
            case 6:
                return   CategoryFragment.newInstance("Politics",title,false);
            case 7:
                return  CategoryFragment.newInstance("Sports",title,false);
            case 8:
                return  CategoryFragment.newInstance("Lifestyle", title,false);
            default:
                return new CategoryFragment();
        }

    }

    @Override
    public int getCount() {
        return 9;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        String title=" ";
        switch (position){
            case 0:
                title="Top Stories";
                break;
            case 1:
                title=Utility.getCountry(context);
                break;
            case 2:
                title="World";
                break;
            case 3:
                title="Entertainment";
                break;
            case 4:
                title="Science And Technology";
                break;
            case 5:
                title="Business";
                break;
            case 6:
                title="Politics";
                break;
            case 7:
                title="Sports";
                break;
            case 8:
                title="Lifestyle";
                break;
        }

        return title;
    }
}
