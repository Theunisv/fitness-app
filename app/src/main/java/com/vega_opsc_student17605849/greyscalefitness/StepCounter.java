package com.vega_opsc_student17605849.greyscalefitness;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.vega_opsc_student17605849.greyscalefitness.repositories.WorkoutRepository;

public class StepCounter extends Service implements SensorEventListener {

    private WorkoutRepository workoutRepo;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d("stepCounter", "Logging steps!");

        workoutRepo = WorkoutRepository.getInstance();

        // Setup Step Counter
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            Toast.makeText(this, "Started Counting Steps", Toast.LENGTH_LONG).show();
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Device not Compatible!", Toast.LENGTH_LONG).show();
            this.stopSelf();
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // Record Step Count
        workoutRepo.userSteps = (int) event.values[0];
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Service destroyed",Toast.LENGTH_SHORT);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


}
