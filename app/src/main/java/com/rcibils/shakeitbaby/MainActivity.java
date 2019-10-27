package com.rcibils.shakeitbaby;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    public static boolean isOpened;
    private TextView txtTorchStatus;
    private TextView txtServiceStatus;
    private Button btnToggleService;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TAG = getString(R.string.app_name);

        txtTorchStatus = findViewById(R.id.txtTorchStatus);
        txtServiceStatus = findViewById(R.id.txtServiceStatus);
        btnToggleService = findViewById(R.id.btnToggleService);

        txtTorchStatus.setText(getString(R.string.torch_status_off));

        btnToggleService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShakeService.class);
                if(ShakeService.isRunning){
                    stopService(intent);
                    txtServiceStatus.setText(getString(R.string.service_status_off));
                    btnToggleService.setText(R.string.btn_service_enable);
                } else {
                    startService(intent);
                    txtServiceStatus.setText(getString(R.string.service_status_on));
                    btnToggleService.setText(R.string.btn_service_disable);
                }
            }
        });

        if(!ShakeService.isRunning) {
            Intent intent = new Intent(this, ShakeService.class);
            startService(intent);
        }
        txtServiceStatus.setText(getString(R.string.service_status_on));
        btnToggleService.setText(R.string.btn_service_disable);

        isOpened = true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        boolean status = intent.getBooleanExtra("torchStatus", false);

        if(status) {
            txtTorchStatus.setText(getString(R.string.torch_status_on));
        } else {
            txtTorchStatus.setText(getString(R.string.torch_status_off));
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
