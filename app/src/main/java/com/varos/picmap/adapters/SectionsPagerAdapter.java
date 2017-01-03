package com.varos.picmap.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.varos.picmap.activities.DetailActivity;
import com.varos.picmap.models.ImageModel;

import java.util.ArrayList;

/**
 * Created by David on 09.12.2016.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter{
    public ArrayList<ImageModel> data = new ArrayList<>();

    public SectionsPagerAdapter(FragmentManager fm, ArrayList<ImageModel> data) {
        super(fm);
        this.data = data;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        return DetailActivity.PlaceholderFragment.newInstance(position, data.get(position).getName(), data.get(position).getUrl());
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return data.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return data.get(position).getName();
    }
}
