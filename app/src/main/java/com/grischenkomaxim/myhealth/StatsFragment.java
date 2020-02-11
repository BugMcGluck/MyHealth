package com.grischenkomaxim.myhealth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class StatsFragment extends Fragment {
    public StatsFragment() {
    }

    public static StatsFragment newInstance() {
        return new StatsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        ViewPager statsViewPager = view.findViewById(R.id.statsViewPager);
        String tabTitles[] = new String[] {getActivity().getString(R.string.textWeight), getActivity().getString(R.string.textBloodPressure), getActivity().getString(R.string.textWater)};
        statsViewPager.setAdapter(new StatsPageFragmentAdapter(getChildFragmentManager(), tabTitles));
        return view;
    }
}
