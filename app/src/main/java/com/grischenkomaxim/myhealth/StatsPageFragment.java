package com.grischenkomaxim.myhealth;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class StatsPageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String DATE = "date";
    public static final String VALUE = "value";
    private Integer mPage;

    public static StatsPageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        StatsPageFragment fragment = new StatsPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPage = getArguments().getInt(ARG_PAGE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats_page, container, false);
        ListView statsPageList = view.findViewById(R.id.statsPageList);
        String[] from = {DATE, VALUE};
        int [] to = {R.id.column_1_item, R.id.column_2_item};
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getContentlist());
        SimpleAdapter adapter = new SimpleAdapter(getContext(),getContentlist(),R.layout.list_item, from, to);
        statsPageList.setAdapter(adapter);

        return view;
    }

    private List<Map<String, String>> getContentlist() {

        class GetContentList extends AsyncTask<Void, Void, List<Map<String, String>> > {

            @Override
            protected List<Map<String, String>> doInBackground(Void... voids) {
                List<Map<String, String>> stringList = new ArrayList<>();
                Map<String, String> m;
                DateFormat[] df = new DateFormat[]{
                        DateFormat.getDateTimeInstance()
                };

                switch (mPage){
                    case 0 :
                        List<Weight> weightList = DataBaseClient.getInstance(getActivity()).getAppDatabase().weightDao().getAll();
                        if (weightList.size() > 0) {
                            for (Weight weight : weightList
                            ) {
                                m = new HashMap<>();
                                m.put(DATE, df[0].format(weight.getDateTime()));
                                m.put(VALUE, String.valueOf(weight.getValue()));
                                //stringList.add(df[0].format(weight.getDateTime()) + " " + weight.getValue());
                                stringList.add(m);
                            }
                        }
                        break;
                    case 1 :
                        List<BloodPressure> bloodPressureList = DataBaseClient.getInstance(getActivity()).getAppDatabase().bloodPressureDao().getAll();
                        if (bloodPressureList.size() > 0) {
                            for (BloodPressure bloodpressure : bloodPressureList
                            ) {
                                m = new HashMap<>();
                                m.put(DATE, df[0].format(bloodpressure.getDateTime()));
                                m.put(VALUE, bloodpressure.getValueSystolic() + "/" + bloodpressure.getValueDiastolic());
                                //stringList.add(df[0].format(bloodpressure.getDateTime()) + " " + bloodpressure.getValueSystolic() + "/" + bloodpressure.getValueDiastolic());
                                stringList.add(m);
                            }
                        }
                        break;
                    case 2 :
                        List<Water> waterList = DataBaseClient.getInstance(getActivity()).getAppDatabase().waterDao().getAll();
                        if (waterList.size() > 0) {
                            for (Water water : waterList
                            ) {
                                m = new HashMap<>();
                                m.put(DATE, df[0].format(water.getDateTime()));
                                m.put(VALUE, String.valueOf(water.getValue()));
                                stringList.add(m);
                            }
                        }
                        break;
                }


                return stringList;
            }

            @Override
            protected void onPostExecute(List<Map<String, String>> stringList) {
                super.onPostExecute(stringList);
            }

        }


        GetContentList gcl = new GetContentList();
        gcl.execute();
        try {
            return gcl.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


}
