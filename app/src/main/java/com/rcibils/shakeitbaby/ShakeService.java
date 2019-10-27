package com.rcibils.shakeitbaby;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;

public class ShakeService extends Service implements SensorEventListener
{
    public static boolean isRunning = false;

    private SensorManager manager;
    private Sensor accelerometerSensor;
    private CameraManager cameraManager;
    private String cameraId;
    private boolean status;

    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7f;
    private static final int SHAKE_SLOP = 1000;
    private static final int SHAKE_TIMEOUT = 3000;
    private static final int SHAKES_REQUIRED = 3;
    private long shakeTime;
    private int shakeCount;

    private String TAG;

    public ShakeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        TAG = getString(R.string.app_name);
        Log.d(TAG, "onStartCommand: service on");
        manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI,
                new Handler());
        status = false;
        cameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try{
            cameraId = cameraManager.getCameraIdList()[0];
        } catch(Exception e){
            Log.e(TAG, "onStartCommand: " + e.getMessage());
        }

        return START_STICKY;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if(gForce > SHAKE_THRESHOLD_GRAVITY){
            final long now = Calendar.getInstance().getTimeInMillis();
            if(shakeTime + SHAKE_SLOP > now){
                return;
            }

            if(shakeTime + SHAKE_TIMEOUT < now){
                shakeCount = 0;
            }

            shakeTime = now;
            ++shakeCount;

            if(shakeCount >= SHAKES_REQUIRED){
                toggleTorch(now);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        manager.unregisterListener(this);
    }

    private void toggleTorch(long now)
    {
        try {
            status = !status;
            cameraManager.setTorchMode(cameraId, status);

            if(MainActivity.isOpened){
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("torchStatus", status);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        } catch(CameraAccessException e){
            Log.e(TAG, "toggleTorch: " + e.getMessage());
        }
    }
}
