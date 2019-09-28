package com.richard.RamHacksCarMax;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
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


//Nick

   public void openCamera(View view){
        Intent intent= new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent);
   }


//end Nick
}


