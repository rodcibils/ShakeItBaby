package com.rcibils.shakeitbaby;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;

public class ShakeService extends Service implements SensorEventListener
{
    private SensorManager manager;
    private Sensor accelerometerSensor;
    private float accel;
    private float curAccel;
    private float lastAccel;
    private boolean posAccelReached;
    private boolean negAccelReached;
    private static final float MAX_ACCEL = 30.0f;
    private static final long MAX_TIME = 500;
    private CameraManager cameraManager;
    private String cameraId;
    private boolean status;
    private long lastStateChange;

    public ShakeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(getString(R.string.app_name), "onStartCommand: servicio iniciado");
        manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI,
                new Handler());
        status = false;
        lastStateChange = Calendar.getInstance().getTime().getTime();
        cameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try{
            cameraId = cameraManager.getCameraIdList()[0];
        } catch(Exception e){
            Log.e(getString(R.string.app_name), "onStartCommand: " + e.getMessage());
        }

        return START_STICKY;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        lastAccel = curAccel;
        curAccel = (float)Math.sqrt((double)(x*x + y*y + z*z));
        float delta = curAccel - lastAccel;
        accel = accel * 0.9f + delta;

        if(accel > 0f && accel > MAX_ACCEL)
            posAccelReached = true;
        if(accel < 0f && accel < -MAX_ACCEL)
            negAccelReached = true;

        if(posAccelReached && negAccelReached){
            posAccelReached = false;
            negAccelReached = false;
            long curTime = Calendar.getInstance().getTime().getTime();
            if(curTime - lastStateChange > MAX_TIME) {
                try{
                    cameraManager.setTorchMode(cameraId, status);
                    lastStateChange = curTime;
                    status = !status;
                    if(MainActivity.isOpened){
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("lanternStatus", status);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                } catch(Exception e){
                    Log.e(getString(R.string.app_name), "onSensorChanged: "
                            + e.getMessage());
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.unregisterListener(this);
    }
}
