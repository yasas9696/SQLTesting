package com.example.sqltesting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sqltesting.helper.HttpJsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CheckoutPopup extends AppCompatActivity {

    private TextView nameView = null;
    private Button checkinButton = null;
    private String itemNameGlobal = null;
    private String itemQRGlobal = null;
    private ImageView clo;
    private final String BASE_URL = "http://www.candyfactorylk.com/blog/movies/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_popup);

        nameView = (TextView) findViewById(R.id.textView7);
        checkinButton = (Button) findViewById(R.id.checkin);

        clo = (ImageView) findViewById(R.id.close);
        clo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//        int width = dm.widthPixels;
//        int height = dm.heightPixels;
//
//        getWindow().setLayout((int)(width*.8),(int)(height*.5));
//
//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.gravity = Gravity.CENTER;
//        params.x = 0;
//        params.y = -20;

        Intent intent =getIntent();

        String itemName = intent.getStringExtra("itemName");
        String itemQR = intent.getStringExtra("itemQR");
        itemNameGlobal = itemName;
        itemQRGlobal = itemQR;

        nameView.setText(itemName);

        checkinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Checkin().execute();
            }
        });

//        getWindow().setAttributes(params);
    }


    private class Checkin extends AsyncTask<String,String,String> {

        private int success = -1;
        private String message = null;

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected String doInBackground(String... strings) {

            HttpJsonParser httpJsonParser1 = new HttpJsonParser();
            Map<String, String> httpParams1 = new HashMap<>();
            httpParams1.put("item_name", itemNameGlobal);
            httpParams1.put("item_qr", itemQRGlobal);

            JSONObject jsonObject1 = httpJsonParser1.makeHttpRequest(
                    BASE_URL + "checkin_item.php", "POST", httpParams1);
            try{

                success = jsonObject1.getInt("success");

                if (success != 1){
                    message = jsonObject1.getString("message");
                }

            }
            catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String result){

            runOnUiThread(new Runnable() {
                public void run() {

                    if(success == 1){
                        Toast.makeText(CheckoutPopup.this, "Item Checked In Successfully." , Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getApplicationContext(),QrActivity.class
                        );
                        startActivityForResult(intent,20);
                    }
                    else{
                        Toast.makeText(CheckoutPopup.this, "Error: " + message , Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

    }
}