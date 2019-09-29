package com.richard.RamHacksCarMax;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;

import static android.content.ContentValues.TAG;

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

        final String stockNum2 = stock_num; //need a final for inner class


   //     getCarInfo(stock_num);

        //**************************8
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {


                    //append stock number to carmax website
                    url += stockNum2;
                    try {
                        //get car web page from carmax website
                        //TODO THIS SHIT DOES NOT WORK
                        Document car_page = Jsoup.connect(url.trim()).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:59.0) Gecko/20100101").get();
                        Log.d(TAG, "we connected with jsoup");

                        //find elements of class price-mileage--value (should be price and mileage from header)
                        Elements price_mileage_values = car_page.select("div.price-mileage--value");

                        //find element that has $ for price
                        for (Element element : price_mileage_values) {
                            String value = element.wholeText();
                            //if value has $, is price were looking for
                            if (value.contains("$")) {
                                price = value;
                                Log.d(TAG, "price found" + price);
                            } else if (value.endsWith("K")) {
                                mileage = value;
                                Log.d(TAG, "value found " + value);

                            }
                            Log.d(TAG, "connected to url succesfully");
                        }

                        //if everything finishes, the web page exists, and car is saleable
                        if(!price.equals("N/A")&&!mileage.equals("N/A"))
                        saleable = true;

                        Log.d(TAG, "SALABLE IS TRUE");

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

                    catch (IOException e) {
                        //IO EXCEPTION THROWN WHEN WEBPAGE DOES NOT EXIST
                        saleable = false;


                        Log.d(TAG, "catch exception made 404");
            /*

            Intent intent = new Intent();
            intent.putExtra("Saleable", saleable);

             */
                    }

                }


                //Your code goes here
                catch (Exception e) {
                    e.printStackTrace();
                }


                TextView saleable_text = findViewById(R.id.saleableTextView);
                TextView price_text = findViewById(R.id.priceTextView);
                TextView mileage_text = findViewById(R.id.mileageTextView);
                ImageView coloredBar = findViewById(R.id.coloredBar);

                if(!saleable){
                    price_text.setVisibility(View.INVISIBLE);
                    mileage_text.setVisibility(View.INVISIBLE);
                    coloredBar.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                    saleable_text.setText("Not Saleable");
                    Log.d(TAG, "salbale is NOT true (if was run)");
                }
                else{
                    saleable_text.setText("Saleable!");
                    price_text.setText(price);
                    mileage_text.setText(mileage);
                    Log.d(TAG, "saleable is true(else is ran)");
                }

            }
        });

        thread.start();
//**************************************************************8888





























}





}
