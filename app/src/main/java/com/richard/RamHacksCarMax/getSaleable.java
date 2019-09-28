package com.richard.RamHacksCarMax;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;

public class getSaleable extends AppCompatActivity {
    private String url = "https://www.carmax.com/cars/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_saleable);

    }

    protected void get_site_info(String stock_num){
        //append stock number to carmax website
        url += stock_num;

        try{
            //get care webpage from carmax website
            Document car_page = Jsoup.connect(url).get();

        }
        catch(IOException e){
            //IO EXCEPTION THROWN WHEN WEBPAGE DOES NOT EXIST

        }

    }


}
