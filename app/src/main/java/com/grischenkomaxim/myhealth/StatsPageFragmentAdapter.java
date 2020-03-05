package com.grischenkomaxim.myhealth;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class StatsPageFragmentAdapter extends FragmentPagerAdapter {
    private static final int PAGE_COUNT = 3;
    String tabTitles[];
    StatsFragment.Types type;

    public StatsPageFragmentAdapter(FragmentManager fm, String tabTitles[], StatsFragment.Types type) {
        super(fm);
        this.tabTitles = tabTitles;
        this.type = type;
    }

    @Override
    public Fragment getItem(int position) {
        return StatsPageFragment.newInstance(position, type);
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
