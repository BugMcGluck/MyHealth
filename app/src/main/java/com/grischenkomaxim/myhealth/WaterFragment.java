package com.grischenkomaxim.myhealth;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
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

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class WaterFragment extends Fragment {

    private float y;
    private TextView drink;
    private ImageView iv;
    private int amplitude = 10;
    private Button buttonOk;
    private int value;
    private static final int FRAME_LENGHT = 20;
    private static final int SHIFT = 20;
    private static final int ATTENUATION = 10;
    private static final int COUNT_WAVES = 5;
    public static final int WAVE_HEIGHT = 300;


    public WaterFragment(){}

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_water, container, false);
        drink = view.findViewById(R.id.drink);
        iv = view.findViewById(R.id.imageView);ArcShape arc = new ArcShape(0, 360);

        final ShapeDrawable shape = new ShapeDrawable(new PathShape(makeWavePath(iv.getWidth(), iv.getHeight(), WAVE_HEIGHT, COUNT_WAVES, amplitude, SHIFT), iv.getWidth(), iv.getHeight()));
        shape.getPaint().setColor(Color.CYAN);
        shape.getPaint().setStyle(Paint.Style.FILL);

      //  shape.setIntrinsicHeight(500);
      //  shape.setIntrinsicWidth(500);

     //  iv.setImageResource(R.drawable.glass);
        iv.setBackground(shape);
      //  iv.getDrawable().setColorFilter(view.getBackground().getColorFilter());
      //  view.setBackgroundColor();
        AnimationDrawable animationDrawable = makeAnimation(iv.getWidth(), iv.getHeight(), WAVE_HEIGHT, COUNT_WAVES, amplitude, FRAME_LENGHT);
        animationDrawable.addFrame(shape,10);

        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                y = motionEvent.getY();
                if(motionEvent.getAction() == MotionEvent.ACTION_MOVE || motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    final ShapeDrawable shape = new ShapeDrawable(new PathShape(makeWavePath(iv.getWidth(), iv.getHeight(), y, COUNT_WAVES, amplitude, SHIFT), iv.getWidth(), iv.getHeight()));
                    shape.getPaint().setColor(Color.CYAN);
                    iv.setBackground(shape);
                    value= (int) (iv.getHeight()- y) * 500 / iv.getHeight();
                    drink.setText(String.valueOf(value) + " мл.");
                }
                return true;
            }
        });

        buttonOk = view.findViewById(R.id.button_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWater();
            }
        });
        return view;
    }

    public static WaterFragment newInstance() {
        return new WaterFragment();
    }

    private  AnimationDrawable makeAnimation(int sizeX, int sizeY, float waveHeight, int countWaves, int amplitude, int frameLenght){
        AnimationDrawable animationDrawable = new AnimationDrawable();
        float shift = 0.0F;
        while (amplitude > 0){
            final ShapeDrawable shape = new ShapeDrawable(new PathShape(makeWavePath(sizeX, sizeY, waveHeight, countWaves, amplitude, shift), iv.getWidth(), iv.getHeight()));
            shape.getPaint().setColor(Color.CYAN);
            shape.getPaint().setStyle(Paint.Style.FILL);
            animationDrawable.addFrame(shape,frameLenght);
            amplitude = amplitude - ATTENUATION;
            shift = shift + SHIFT;
        }
        return animationDrawable;
    }

    private Path makeWavePath(int sizeX, int sizeY, float waveHeight, int countWaves, int amplitude, float shift){
        float waveQuarterLength = (float) sizeX / countWaves / 4;
        Path path = new Path();
        path.reset();
        path.moveTo(sizeX, waveHeight);
        path.lineTo(sizeX, sizeY);
        path.lineTo(0, sizeY);
        path.lineTo(0, waveHeight);
        for (int i = 0; i < countWaves; i++){
            if (shift >= (waveQuarterLength * 2)){
                shift = shift - (waveQuarterLength * 2);
            }
            path.rQuadTo(waveQuarterLength + shift,amplitude * 2, waveQuarterLength * 2 + shift, 0);
            path.rQuadTo(waveQuarterLength + shift,-amplitude * 2, waveQuarterLength * 2 + shift, 0);
        }
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

}
