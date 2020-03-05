package com.grischenkomaxim.myhealth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class StatsFragment extends Fragment {
    enum Types  {
        STATS,
        CHARTS
    }
    Types type;
    public StatsFragment(Types type) {
        this.type = type;
    }

    public static StatsFragment newInstance(Types type) {
        return new StatsFragment(type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        ViewPager statsViewPager = view.findViewById(R.id.statsViewPager);
        String tabTitles[] = new String[] {getActivity().getString(R.string.textWeight), getActivity().getString(R.string.textBloodPressure), getActivity().getString(R.string.textWater)};
        switch (type){
            case STATS:
                statsViewPager.setAdapter(new StatsPageFragmentAdapter(getChildFragmentManager(), tabTitles, type));
                break;
            case CHARTS:
                statsViewPager.setAdapter(new ChartPageFragmentAdapter(getChildFragmentManager(), tabTitles, type));
                break;
        }
        statsViewPager.setAdapter(new StatsPageFragmentAdapter(getChildFragmentManager(), tabTitles, type));
        return view;
    }
}
