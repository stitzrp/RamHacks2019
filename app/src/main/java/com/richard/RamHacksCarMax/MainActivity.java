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
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private Button qr_page_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String stockNum=""; //the stock number

        //TODO uncomment the line below once the stocknumber is found from the QR code
        //checkSalable(stockNum);
    }









//Javier

    //method launches the check saleable activity and passes number as parameter
    private void checkSalable( String num ){


        // create and launch intent
        final Intent saleCheck = new Intent(MainActivity.this,getSaleable.class);

        Bundle bundle = new Bundle();
        bundle.putString("num", num);
        saleCheck.putExtra("bundle", bundle);
        startActivity(saleCheck);
    }
//end Javier



    public void openCam(View view){
        checkCameraHardware(getApplicationContext());
            
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

    /// start Sean

    private void qrGenerationActivity(View view) {
        Intent qr_intent = new Intent(this, qrgenerator_activity.class);
        startActivity(qr_intent);
    }

    /// end Sean
}


