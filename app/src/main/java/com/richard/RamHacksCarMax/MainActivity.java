package com.richard.RamHacksCarMax;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

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
}
