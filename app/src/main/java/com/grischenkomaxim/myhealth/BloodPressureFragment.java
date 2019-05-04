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
import java.util.concurrent.ExecutionException;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class BloodPressureFragment extends Fragment {
    public BloodPressureFragment() {
    }
    NumberPicker bloodPressurePickerSystolic, bloodPressurePickerDiastolic;
    Button buttonOk;
    TextView bloodPressuretTime, bloodPressureDate;
    public static BloodPressureFragment newInstance() {
        return new BloodPressureFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blood_pressure, container, false);
        bloodPressurePickerSystolic = view.findViewById(R.id.bloodPressurePickerSystolic);
        bloodPressurePickerSystolic.setMaxValue(250);
        bloodPressurePickerSystolic.setMinValue(50);
        bloodPressurePickerDiastolic = view.findViewById(R.id.bloodPressurePickerDiastolic);
        bloodPressurePickerDiastolic.setMaxValue(130);
        bloodPressurePickerDiastolic.setMinValue(40);
        BloodPressure lastBloodPressure = getLastBloodPressure();
        if (lastBloodPressure != null){
            bloodPressurePickerSystolic.setValue(lastBloodPressure.getValueSystolic());
            bloodPressurePickerDiastolic.setValue(lastBloodPressure.getValueDiastolic());
        }
        buttonOk = view.findViewById(R.id.button_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBloodPressure(bloodPressurePickerSystolic.getValue(), bloodPressurePickerDiastolic.getValue());
            }
        });
        bloodPressuretTime = view.findViewById(R.id.bloodPressureTime);
        bloodPressuretTime.setText((Calendar.getInstance().get(HOUR_OF_DAY) < 10 ? "0" + Calendar.getInstance().get(HOUR_OF_DAY) : Calendar.getInstance().get(HOUR_OF_DAY)) + ":" + (Calendar.getInstance().get(MINUTE) < 10 ? "0" + Calendar.getInstance().get(MINUTE) : Calendar.getInstance().get(MINUTE)));
        bloodPressuretTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeDialog();
            }
        });
        bloodPressureDate = view.findViewById(R.id.bloodPressureDate);
        bloodPressureDate.setText(
                (Calendar.getInstance().get(DAY_OF_MONTH) < 10 ? "0" + Calendar.getInstance().get(DAY_OF_MONTH) : Calendar.getInstance().get(DAY_OF_MONTH)) +
                        "." +
                        (Calendar.getInstance().get(MONTH) < 9 ? "0" + (Calendar.getInstance().get(MONTH) + 1) : (Calendar.getInstance().get(MONTH) + 1)) +
                        "." +
                        Calendar.getInstance().get(YEAR)
        );
        bloodPressureDate.setOnClickListener(new View.OnClickListener() {
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
                bloodPressureDate.setText((dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "." + (month < 9 ? "0" + (month + 1) : (month + 1)) + "." + year);
            }
        }, Calendar.getInstance().get(YEAR), Calendar.getInstance().get(MONTH), Calendar.getInstance().get(DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void openTimeDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                bloodPressuretTime.setText((hourOfDay < 10 ? "0" + hourOfDay : hourOfDay) + ":" + (minute < 10 ? "0" + minute : minute));
            }
        }, Calendar.getInstance().get(HOUR_OF_DAY), Calendar.getInstance().get(MINUTE), true);
        timePickerDialog.show();
    }
    private void saveBloodPressure(int bloodPressureSystolic, int bloodPressureDiastolic) {
        final BloodPressure newBloodPressure  = new BloodPressure();
        newBloodPressure.setTime(new Date());
        newBloodPressure.setValueSystolic(bloodPressureSystolic);
        newBloodPressure.setValueDiastolic(bloodPressureDiastolic);
        newBloodPressure.setUpdate_time();

        class SaveBloodPressure extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DataBaseClient.getInstance(getActivity()).getAppDatabase().bloodPressureDao().insert(newBloodPressure);
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
        builder.setMessage("Записать давление " + String.valueOf(bloodPressureSystolic) + "/" + String.valueOf(bloodPressureDiastolic) + " " + bloodPressureDate.getText() + " " + bloodPressuretTime.getText() + "?");
        builder.setTitle("Записать давление");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SaveBloodPressure sbp = new SaveBloodPressure();
                sbp.execute();
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

    private BloodPressure getLastBloodPressure() {

        class GetLastBloodPressure extends AsyncTask<Void, Void, BloodPressure> {

            @Override
            protected BloodPressure doInBackground(Void... voids) {
                BloodPressure lastBloodPressure;
                lastBloodPressure = DataBaseClient.getInstance(getActivity()).getAppDatabase().bloodPressureDao().getLast();
                if (lastBloodPressure != null)
                    return lastBloodPressure;
                else return null;
            }

            @Override
            protected void onPostExecute(BloodPressure aVoid) {
                super.onPostExecute(aVoid);
            }

        }

        GetLastBloodPressure glbp = new GetLastBloodPressure();
        glbp.execute();
        try {
            return glbp.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
