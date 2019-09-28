package com.richard.RamHacksCarMax;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;

public class getSaleable extends AppCompatActivity {
    private String url = "https://www.carmax.com/cars/";
    private boolean saleable = false;
    private String price = "N/A";
    private String mileage = "N/A";
    private static final String TAG = "get_saleable"; //just a log to debug stuff

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_saleable);




        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);



        //Javier Moreira
        String stock_num=""; //Unique Stock number of car
        // unpack stocknumber  from main activity:
        Bundle stockBundle = getIntent().getBundleExtra("bundle");
        stock_num =  stockBundle.getString("num");
        Log.d(TAG, "num value passed in ----->" + stock_num);
        //end JAvier

        getCarInfo(stock_num);

        TextView saleable_text = findViewById(R.id.saleableTextView);
        TextView price_text = findViewById(R.id.priceTextView);
        TextView mileage_text = findViewById(R.id.mileageTextView);

        if(!saleable){
            price_text.setVisibility(View.INVISIBLE);
            mileage_text.setVisibility(View.INVISIBLE);
            saleable_text.setText("Not Saleable");
        }
        else{
            saleable_text.setText("Saleable!");
            price_text.setText(price);
            mileage_text.setText(mileage);
        }

    }

    protected void getCarInfo(String stock_num){
        //append stock number to carmax website
        url += stock_num;
        try{
            //get car web page from carmax website
            //TODO THIS SHIT DOES NOT WORK
            Document car_page = Jsoup.connect(url).get();

            //find elements of class price-mileage--value (should be price and mileage from header)
            Elements price_mileage_values = car_page.select("div.price-mileage--value");

            //find element that has $ for price
            for(Element element: price_mileage_values){
                String value = element.wholeText();
                //if value has $, is price were looking for
                if(value.contains("$")){
                    price = value;
                }
                else if(value.endsWith("K")){
                    mileage = value;
                }
            }

            //if everything finishes, the web page exists, and car is saleable
            saleable = true;

            /*
             Commented out, not returning for intent

            //create package to return result
            Intent intent = new Intent();
            intent.putExtra("Saleable", saleable);
            intent.putExtra("Price", price);
            intent.putExtra("Mileage", mileage);
            setResult(RESULT_OK, intent);
            finish();

            */

        }
        catch(IOException e){
            //IO EXCEPTION THROWN WHEN WEBPAGE DOES NOT EXIST
            saleable = false;

            /*

            Intent intent = new Intent();
            intent.putExtra("Saleable", saleable);

             */
        }

    }


}
