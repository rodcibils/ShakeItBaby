package com.rcibils.shakeitbaby;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    public static boolean isOpened;
    private TextView txtTorchState;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TAG = getString(R.string.app_name);

        txtTorchState = findViewById(R.id.txtTorchState);
        txtTorchState.setText("Torch OFF");

        Intent intent = new Intent(this, ShakeService.class);
        startService(intent);

        isOpened = true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        boolean status = intent.getBooleanExtra("torchStatus", false);

        if(status) {
            txtTorchState.setText("Torch ON");
        } else {
            txtTorchState.setText("Torch OFF");
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
