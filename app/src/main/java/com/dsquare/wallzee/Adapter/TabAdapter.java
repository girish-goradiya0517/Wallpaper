package com.dsquare.wallzee.Adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dsquare.wallzee.Fragment.FragmentSearchWall;
import com.dsquare.wallzee.Fragment.RingtoneFragmentSearch;

public class TabAdapter extends FragmentPagerAdapter {

    Context context;
    int totalTabs;

    public TabAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentSearchWall homeFragment = new FragmentSearchWall();
                return homeFragment;
            case 1:
                RingtoneFragmentSearch photoFragment = new RingtoneFragmentSearch();
                return photoFragment;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}
