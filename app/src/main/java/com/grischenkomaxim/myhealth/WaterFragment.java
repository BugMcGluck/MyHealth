package com.grischenkomaxim.myhealth;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class WaterFragment extends Fragment {

    private float lastWaterHeight = 0;
    private TextView drink, dayDrink;
    private ImageView iv;
    private Button buttonOk;
    private int value;
    private AnimationDrawable animationDrawable = null;
    private static final int FRAME_LENGTH = 25;
    private static final int SHIFT = 20;
    private static final int ATTENUATION = 2;
    private static final int COUNT_WAVES = 5;
    private static final int AMPLITUDE = 30;
    private static final int MAX_RANGE = 100; //диапазон изменения при котором достигается максимальная амплитуда


    public WaterFragment(){}

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_water, container, false);
        drink = view.findViewById(R.id.drink);
        dayDrink = view.findViewById(R.id.dayDrink);
        dayDrink.setText(getDayWater() + " мл.");
        iv = view.findViewById(R.id.imageView);
        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float y = motionEvent.getY();
                 if(motionEvent.getAction() == MotionEvent.ACTION_MOVE || motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    if (animationDrawable != null && animationDrawable.isRunning()) {
                        animationDrawable.stop();
                    }
                    float range = Math.abs(lastWaterHeight - y);
                    int amplitude;
                    if (range >= MAX_RANGE){
                        amplitude = AMPLITUDE;
                    }else {
                        amplitude = (int) (AMPLITUDE * (range / MAX_RANGE));
                    }
                    animationDrawable = makeAnimation(iv.getWidth(), iv.getHeight(), y, COUNT_WAVES, amplitude, FRAME_LENGTH);
                    iv.setBackground(animationDrawable);
                    animationDrawable.start();
                    value= (int) (iv.getHeight()- y) * 500 / iv.getHeight();
                    drink.setText(String.valueOf(value) + " мл.");
                    lastWaterHeight = y;
                }
                return true;
            }
        });

        buttonOk = view.findViewById(R.id.button_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWater();
                dayDrink.setText(getDayWater() + " мл.");
            }
        });
        return view;
    }

    public static WaterFragment newInstance() {
        return new WaterFragment();
    }

    private  AnimationDrawable makeAnimation(int sizeX, int sizeY, float waveHeight, int countWaves, int amplitude, int frameLenght){
        AnimationDrawable animationDrawable = new AnimationDrawable();
        ShapeDrawable shape;
        float shift = 0.0F;
        boolean shiftDirection = new Random().nextBoolean();
        while (amplitude > 0){
            shape = new ShapeDrawable(new PathShape(makeWavePath(sizeX, sizeY, waveHeight, countWaves, amplitude, shift), iv.getWidth(), iv.getHeight()));
            shape.getPaint().setColor(Color.CYAN);
            shape.getPaint().setStyle(Paint.Style.FILL);
            animationDrawable.addFrame(shape,frameLenght);
            amplitude = amplitude - ATTENUATION;
            if (shiftDirection) {
                shift = shift + SHIFT;
            } else {
                shift = shift - SHIFT;
            }
        }
        shape = new ShapeDrawable(new PathShape(makeFlatPath(sizeX, sizeY, waveHeight), iv.getWidth(), iv.getHeight()));
        shape.getPaint().setColor(Color.CYAN);
        shape.getPaint().setStyle(Paint.Style.FILL);
        animationDrawable.addFrame(shape,frameLenght);
        animationDrawable.setOneShot(true);
        return animationDrawable;
    }

    private Path makeWavePath(int sizeX, int sizeY, float waveHeight, int countWaves, int amplitude, float shift){
        float waveQuarterLength = (float) sizeX / countWaves / 4;
        if (Math.abs(shift) >= (sizeX / countWaves)) {
            if (shift > 0) {
                shift = shift - (sizeX / countWaves);
            }else {
                shift = shift + (sizeX / countWaves);
            }
        }
        Path path = new Path();
        path.reset();
        path.moveTo(sizeX, waveHeight);
        path.lineTo(sizeX, sizeY);
        path.lineTo(0, sizeY);
        path.lineTo(0, waveHeight);
        path.lineTo(shift, waveHeight);
        for (int i = 0; i < countWaves; i++){
            path.rQuadTo(waveQuarterLength ,amplitude * 2, waveQuarterLength * 2 ,0);
            path.rQuadTo(waveQuarterLength ,-amplitude * 2, waveQuarterLength * 2 ,0);
        }
        path.close();
        return path;
    }

    private Path makeFlatPath(int sizeX, int sizeY, float waveHeight){
        Path path = new Path();
        path.reset();
        path.moveTo(sizeX, waveHeight);
        path.lineTo(sizeX, sizeY);
        path.lineTo(0, sizeY);
        path.lineTo(0, waveHeight);
        path.close();
        return path;
    }

    private void saveWater() {
        final Water newWater = new Water();
        newWater.setTime(new Date());
        newWater.setValue(value);
        newWater.setUpdate_time();
        class SaveWater extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DataBaseClient.getInstance(getActivity()).getAppDatabase().waterDao().insert(newWater);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getActivity(), getString(R.string.Saved), Toast.LENGTH_LONG).show();
            }

        }
        SaveWater sw = new SaveWater();
        sw.execute();
    }

    private int getDayWater() {

        class GetDayWater extends AsyncTask<Void, Void, Integer> {

            @Override
            protected Integer doInBackground(Void... voids) {
                int dayWater;
                LocalDate localDate = LocalDate.now();
                dayWater = DataBaseClient.getInstance(getActivity()).getAppDatabase().waterDao().getDayValue(localDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000);
                return dayWater;
            }

            @Override
            protected void onPostExecute(Integer aVoid) {
                super.onPostExecute(aVoid);
            }

        }

        GetDayWater gdw = new GetDayWater();
        gdw.execute();
        try {
            return gdw.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
