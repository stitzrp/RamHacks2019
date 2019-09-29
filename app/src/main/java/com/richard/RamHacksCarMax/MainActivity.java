package com.richard.RamHacksCarMax;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.*;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }











    public void openCam(View view){
        checkCameraHardware(getApplicationContext());
            Intent intent = new Intent(MainActivity.this,CameraActivity.class);
            startActivity(intent);
        }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void open_qr_generator(View view) {
        Intent qr_generator_intent = new Intent(MainActivity.this, qrgenerator_activity.class);
        startActivity(qr_generator_intent);
    }

}


