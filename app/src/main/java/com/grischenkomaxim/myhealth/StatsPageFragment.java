package com.grischenkomaxim.myhealth;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class StatsPageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String DATE = "date";
    public static final String VALUE = "value";
    public static final String TYPE = "type";
    private Integer mPage;
    private StatsFragment.Types type;
    //private  static Bundle args = new Bundle();

    public static StatsPageFragment newInstance(int page, StatsFragment.Types type) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putSerializable(TYPE, type);
        StatsPageFragment fragment = new StatsPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPage = getArguments().getInt(ARG_PAGE);
            type = (StatsFragment.Types) getArguments().getSerializable(TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        //StatsFragment.Types type = (StatsFragment.Types) args.getSerializable(TYPE);
        switch (type) {
            case STATS:
                view = createStatsPage(inflater, container);
                break;
            case CHARTS:
                view = createChartPage(inflater, container);
                break;
        }
        return view;
    }

    private View createStatsPage(LayoutInflater inflater, ViewGroup container){
        View view = inflater.inflate(R.layout.fragment_stats_page, container, false);
        ListView statsPageList = view.findViewById(R.id.statsPageList);
        String[] from = {DATE, VALUE};
        int [] to = {R.id.column_1_item, R.id.column_2_item};
        SimpleAdapter adapter = new SimpleAdapter(getContext(),getContentlist(),R.layout.list_item, from, to);
        statsPageList.setAdapter(adapter);
        return view;
    }

    private View createChartPage(LayoutInflater inflater, ViewGroup container){
        View view = inflater.inflate(R.layout.fragment_chart_page, container, false);
        GraphView graph = view.findViewById(R.id.chart);
        List <DataPoint> dataPoints = new ArrayList<>();
        List <DataPoint> dataPoints2 = new ArrayList<>();
        LineGraphSeries<DataPoint> series;
        switch (mPage) {
            case 0:
                List<Weight>  weightList = (List<Weight>)(List<?>)getChartData();
                Collections.sort(weightList, new Comparator<Weight>() {
                    public int compare(Weight o1, Weight o2) {
                        return o1.getDateTime().compareTo(o2.getDateTime());
                    }
                });
                for (Weight weight: weightList) {
                    dataPoints.add(new DataPoint(weight.getDateTime(), (double)weight.getValue()));
                }
                break;
            case 1:
                List<BloodPressure>  bloodPressureList = (List<BloodPressure>)(List<?>)getChartData();
                Collections.sort(bloodPressureList, new Comparator<BloodPressure>() {
                    public int compare(BloodPressure o1, BloodPressure o2) {
                        return o1.getDateTime().compareTo(o2.getDateTime());
                    }
                });
                for (BloodPressure bloodPressure: bloodPressureList) {
                    dataPoints.add(new DataPoint(bloodPressure.getDateTime(), (double)bloodPressure.getValueDiastolic()));
                    dataPoints2.add(new DataPoint(bloodPressure.getDateTime(), (double)bloodPressure.getValueSystolic()));
                }
                break;
            case 2:
                List<Water>  waterList = (List<Water>)(List<?>)getChartData();
                Map<Date, Double> values = new TreeMap<>();
                LocalDate localDate;
                //Date date = new Date();
                Double value, addValue = new Double(0);
//                for (Water water: waterList) { // заполняет мап датами
//                    localDate = water.getDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//                    date.setTime(localDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
//                    values.put(date, null);
//                }
                for (Water water: waterList) { // добавляем в мап суммарное значение за дату

                    Date date = new Date();

                    value = Double.valueOf((double) water.getValue());
                    localDate = water.getDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    date.setTime(localDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000);
                    addValue = values.get(date);
                    if (addValue != null && !addValue.isNaN()){
                        value = value + addValue;
                        values.replace(date, value);
                    }else {
                        values.put(date, value);
                    }

                }
//                Collections.sort(values, new Comparator<Date, Double>() {
//                    public int compare(Date o1, Date o2) {
//                        return o1.compareTo(o2);
//                    }
//                });

                for (Map.Entry<Date, Double> entry : values.entrySet()) {
                    dataPoints.add(new DataPoint(entry.getKey(), entry.getValue()));
                }
                break;
        }
        DataPoint[] dp = new DataPoint[dataPoints.size()];
        int i = 0;
        for (DataPoint dataPoint:
                dataPoints) {
            dp[i++] = dataPoint;
        }
        series = new LineGraphSeries<>(dp);
        graph.addSeries(series);
        if (dataPoints2 != null && dataPoints2.size() > 0) {
            DataPoint[] dp2 = new DataPoint[dataPoints2.size()];
            i = 0;
            for (DataPoint dataPoint:
                    dataPoints2) {
                dp2[i++] = dataPoint;
            }
            series = new LineGraphSeries<>(dp2);
            series.setColor(Color.RED);
            graph.addSeries(series);
        }
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getViewport().setScalable(true);

        return view;
    }

    private List<Object> getChartData(){
        class GetChartData extends AsyncTask<Void, Void, List<Object>>{

            @Override
            protected List<Object> doInBackground(Void... voids) {
                List<Object> result = new ArrayList<>();

                switch (mPage){
                    case 0 :
                        result.addAll(DataBaseClient.getInstance(getActivity()).getAppDatabase().weightDao().getAll());
                        break;
                    case 1 :
                        result.addAll(DataBaseClient.getInstance(getActivity()).getAppDatabase().bloodPressureDao().getAll());
                        break;
                    case 2 :
                        result.addAll(DataBaseClient.getInstance(getActivity()).getAppDatabase().waterDao().getAll());
                        break;
                }
                return result;
            }
        }
        GetChartData gcd = new GetChartData();
        gcd.execute();
        try {
            return gcd.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
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
