package com.grischenkomaxim.myhealth;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class WeightFragment extends Fragment {

    NumberPicker weightPickerPrime, weightPickerSecond;
    Button buttonOk;
    TextView weightTime, weightDate;
    public WeightFragment() {
    }

    public static WeightFragment newInstance() {
        return new WeightFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weight, container, false);
        weightPickerPrime = (NumberPicker) view.findViewById(R.id.weightPickerPrime);
        weightPickerPrime.setMaxValue(150);
        weightPickerPrime.setMinValue(0);
        weightPickerSecond = (NumberPicker) view.findViewById(R.id.weightPickerSecond);
        weightPickerSecond.setMaxValue(9);
        weightPickerSecond.setMinValue(0);
        weightPickerSecond.setDisplayedValues(new String[] {"000", "100", "200", "300", "400", "500", "600", "700", "800", "900"});
        buttonOk = view.findViewById(R.id.button_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWeight(weightPickerPrime, weightPickerSecond);
            }
        });
        weightTime = view.findViewById(R.id.weightTime);
        weightTime.setText((Calendar.getInstance().get(HOUR_OF_DAY) < 10 ? "0" + Calendar.getInstance().get(HOUR_OF_DAY) : Calendar.getInstance().get(HOUR_OF_DAY)) + ":" + (Calendar.getInstance().get(MINUTE) < 10 ? "0" + Calendar.getInstance().get(MINUTE) : Calendar.getInstance().get(MINUTE)));
        weightTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeDialog();
            }
        });
        weightDate = view.findViewById(R.id.weightDate);
        weightDate.setText(
                (Calendar.getInstance().get(DAY_OF_MONTH) < 10 ? "0" + Calendar.getInstance().get(DAY_OF_MONTH) : Calendar.getInstance().get(DAY_OF_MONTH)) +
                "." +
                (Calendar.getInstance().get(MONTH) < 9 ? "0" + (Calendar.getInstance().get(MONTH) + 1) : (Calendar.getInstance().get(MONTH) + 1)) +
                "." +
                Calendar.getInstance().get(YEAR)
        );
        weightDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateDialog();
            }
        });
        return view;
    }

    private void openDateDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                weightDate.setText((dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "." + (month < 9 ? "0" + (month + 1) : (month + 1)) + "." + year);
            }
        }, Calendar.getInstance().get(YEAR), Calendar.getInstance().get(MONTH), Calendar.getInstance().get(DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void openTimeDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                weightTime.setText((hourOfDay < 10 ? "0" + hourOfDay : hourOfDay) + ":" + (minute < 10 ? "0" + minute : minute));
            }
        }, Calendar.getInstance().get(HOUR_OF_DAY), Calendar.getInstance().get(MINUTE), true);
        timePickerDialog.show();
    }

    private void saveWeight(NumberPicker weightPickerPrime, NumberPicker weightPickerSecond) {
        final Weight newWeight  = new Weight();
        newWeight.setTime(new Date());
        float newMass = weightPickerPrime.getValue() + weightPickerSecond.getValue()* 0.1F;
        newWeight.setValue(newMass);
        newWeight.setUpdate_time();

        class SaveWeight extends AsyncTask <Void, Void, Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                DataBaseClient.getInstance(getActivity()).getAppDatabase().weightDao().insert(newWeight);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            //    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                Toast.makeText(getActivity(), getString(R.string.Saved), Toast.LENGTH_LONG).show();
            }

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Записать вес " + String.valueOf(newMass) + "кг. " + weightDate.getText() + " " + weightTime.getText() + "?");
        builder.setTitle("Записать вес");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SaveWeight sw = new SaveWeight();
                sw.execute();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
