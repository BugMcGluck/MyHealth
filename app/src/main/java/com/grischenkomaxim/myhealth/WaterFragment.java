package com.grischenkomaxim.myhealth;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
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

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Date;


public class WaterFragment extends Fragment {
    float y;
    TextView drink;
    ImageView iv;
    int countWaves = 5;
    int amplitude = 10;
    Button buttonOk;
    int value;

    public WaterFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_water, container, false);
        drink = view.findViewById(R.id.drink);
        iv = view.findViewById(R.id.imageView);ArcShape arc = new ArcShape(0, 360);

        final ShapeDrawable shape = new ShapeDrawable(new PathShape(makeWavePath(iv.getWidth(), iv.getHeight(), 300, countWaves, amplitude), iv.getWidth(), iv.getHeight()));
        shape.getPaint().setColor(Color.CYAN);
        shape.getPaint().setStyle(Paint.Style.FILL);

      //  shape.setIntrinsicHeight(500);
      //  shape.setIntrinsicWidth(500);

     //  iv.setImageResource(R.drawable.glass);
        iv.setBackground(shape);
      //  iv.getDrawable().setColorFilter(view.getBackground().getColorFilter());
      //  view.setBackgroundColor();

        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                y = motionEvent.getY();
                if(motionEvent.getAction() == MotionEvent.ACTION_MOVE || motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    final ShapeDrawable shape = new ShapeDrawable(new PathShape(makeWavePath(iv.getWidth(), iv.getHeight(), y, countWaves, amplitude), iv.getWidth(), iv.getHeight()));
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


    private Path makeWavePath(int sizeX, int sizeY, float waveHeight, int countWaves, int amplitude){
        float waveQuarterLength = (float) sizeX / countWaves / 4;
        Path path = new Path();
        path.reset();
        path.moveTo(sizeX, waveHeight);
        path.lineTo(sizeX, sizeY);
        path.lineTo(0, sizeY);
        path.lineTo(0, waveHeight);
        for (int i = 0; i < countWaves; i++){
            path.rQuadTo(waveQuarterLength,amplitude * 2, waveQuarterLength * 2, 0);
            path.rQuadTo(waveQuarterLength,-amplitude * 2, waveQuarterLength * 2, 0);
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
