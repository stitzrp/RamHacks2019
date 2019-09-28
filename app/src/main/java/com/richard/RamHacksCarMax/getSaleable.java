package com.richard.RamHacksCarMax;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class getSaleable extends AppCompatActivity {
    private String url = "https://www.carmax.com/cars/";
    private boolean saleable = false;
    private String price = "N/A";
    private String mileage = "N/A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_saleable);

    }

    protected void getCarInfo(String stock_num){
        //append stock number to carmax website
        url += stock_num;
        try{
            //get car web page from carmax website
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

            //create package to return result
            Intent intent = new Intent();
            intent.putExtra("Saleable", saleable);
            intent.putExtra("Price", price);
            intent.putExtra("Mileage", mileage);
            setResult(RESULT_OK, intent);
            finish();

        }
        catch(IOException e){
            //IO EXCEPTION THROWN WHEN WEBPAGE DOES NOT EXIST
            saleable = false;
            Intent intent = new Intent();
            intent.putExtra("Saleable", saleable);
        }

    }


}
