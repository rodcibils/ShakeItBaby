package com.rcibils.shakeitbaby;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    public static boolean isOpened;
    private TextView txtLanternState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLanternState = findViewById(R.id.txtLanternState);
        txtLanternState.setText("Lantern OFF");

        Intent intent = new Intent(this, ShakeService.class);
        startService(intent);

        isOpened = true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        boolean state = intent.getBooleanExtra("lanternStatus", false);

        if(state) {
            Log.d(getString(R.string.app_name), "onNewIntent: on");
            txtLanternState.setText("Lantern ON");
        } else {
            Log.d(getString(R.string.app_name), "onNewIntent: off");
            txtLanternState.setText("Lantern OFF");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isOpened = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOpened = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isOpened = true;
    }
}
