package com.example.root.cardsuitprediction2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ChooseASuit extends AppCompatActivity implements SensorEventListener {

    private final float NOISE = (float) 10.0;
    private boolean mInitialized; // used for initializing sensor only once
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private ImageView suitImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_asuit);


        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        suitImageView = (ImageView) findViewById(R.id.clubsImageView);
        suitImageView.setVisibility(View.INVISIBLE);

        Button clubsButton = (Button)findViewById(R.id.clubsbutton);

        clubsButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick (View v) {
                        suitImageView = (ImageView) findViewById(R.id.clubsImageView);
                        suitImageView.setVisibility(View.VISIBLE);

                    }
                }
        );

        Button resetButton = (Button)findViewById(R.id.resetbutton);

        resetButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick (View v) {
                        suitImageView.setVisibility(View.INVISIBLE);
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this, mSensor);
        super.onPause();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {

        // values[0]: Acceleration minus Gx on the x-axis
        // values[1]: Acceleration minus Gx on the y-axis
        // values[2]: Acceleration minus Gx on the z-axis

        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];
        double mLastX = 0.0;
        double mLastY = 0.0;
        double mLastZ = 0.0;


        final double ALPHA = 0.8;


        double[] gravity = {0, 0, 0};

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * event.values[0];
        gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * event.values[1];
        gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        x = event.values[0] - gravity[0];
        y = event.values[1] - gravity[1];
        z = event.values[2] - gravity[2];

        if (!mInitialized) {
            // sensor is used for the first time, initialize the last read values
             mLastX = x;
             mLastY = y;
             mLastZ = z;
            mInitialized = true;
        } else {
            // sensor is already initialized, and we have previously read values.
            // take difference of past and current values and decide which
            // axis acceleration was detected by comparing values

            double deltaX = Math.abs(mLastX - x);
            double deltaY = Math.abs(mLastY - y);
            double deltaZ = Math.abs(mLastZ - z);
            if (deltaX < NOISE)
                deltaX = (float) 0.0;
            if (deltaY < NOISE)
                deltaY = (float) 0.0;
            if (deltaZ < NOISE)
                deltaZ = (float) 0.0;

            mLastX = x;
            mLastY = y;
            mLastZ = z;


            if (deltaX > deltaY) {
                suitImageView.setVisibility(View.INVISIBLE);
                suitImageView = (ImageView) findViewById(R.id.heartsImageView);
                suitImageView.setVisibility(View.VISIBLE);

            } else if (deltaY > deltaX) {
                suitImageView.setVisibility(View.INVISIBLE);
                suitImageView = (ImageView) findViewById(R.id.spadesImageView);
                suitImageView.setVisibility(View.VISIBLE);

            } else if ((deltaZ > deltaX) && (deltaZ > deltaY)) {
                // Z shake
                suitImageView.setVisibility(View.INVISIBLE);
                suitImageView = (ImageView) findViewById(R.id.diamondsImageView);
                suitImageView.setVisibility(View.VISIBLE);

            }

        }

    }
}
