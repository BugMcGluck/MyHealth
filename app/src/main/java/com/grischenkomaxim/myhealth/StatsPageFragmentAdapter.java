package com.grischenkomaxim.myhealth;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class StatsPageFragmentAdapter extends FragmentPagerAdapter {
    private static final int PAGE_COUNT = 2;
    String tabTitles[];

    public StatsPageFragmentAdapter(FragmentManager fm, String tabTitles[]) {
        super(fm);
        this.tabTitles = tabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        return StatsPageFragment.newInstance(position);
     }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
