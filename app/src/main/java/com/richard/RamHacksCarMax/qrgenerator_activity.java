package com.richard.RamHacksCarMax;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class qrgenerator_activity extends AppCompatActivity {

//    private Button generate_qr_button;
//    private EditText qr_input_field;
//    private ImageView qr;
//    private Bitmap bitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgenerator);

        final  Button generate_qr_button = findViewById(R.id.button2);
        final EditText qr_input_field= findViewById(R.id.editText);
        final  ImageView qr =findViewById(R.id.qrCode);


        Button generate_qr_button2 = generate_qr_button;
         EditText qr_input_field2= qr_input_field;
         ImageView qr2 =qr;

        generate_qr_button.setOnClickListener(new View.OnClickListener(){
           public void onClick(View view) {
               String input = qr_input_field.getText().toString().trim();

               if(input != null) {
                   try {
                       MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                       BitMatrix bitMatrix = multiFormatWriter.encode(input, BarcodeFormat.QR_CODE, 500, 500);
                       BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                       Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                       qr.setImageBitmap(bitmap);

                   }
                   catch (WriterException w)
                   {
                       w.printStackTrace();
                   }
               }
           }
        });

    }

}
