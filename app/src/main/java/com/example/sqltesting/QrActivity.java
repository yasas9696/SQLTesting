package com.example.sqltesting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sqltesting.helper.HttpJsonParser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class QrActivity extends AppCompatActivity {
    ImageView imageView;
    Button button;
    Button btnScan;
    EditText editText;
    String EditTextValue ;
    Thread thread ;
    private static final String BASE_URL = "http://www.candyfactorylk.com/blog/movies/";
    public final static int QRcodeWidth = 350 ;
    private String itemQRCode = null;
    Bitmap bitmap ;

    TextView tv_qr_readTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        imageView = (ImageView)findViewById(R.id.imageView);
        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);
        btnScan = (Button)findViewById(R.id.btnScan);
        tv_qr_readTxt = (TextView) findViewById(R.id.tv_qr_readTxt);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editText.getText().toString().isEmpty()){
                    EditTextValue = editText.getText().toString();

                    try {
                        bitmap = TextToImageEncode(EditTextValue);

                        imageView.setImageBitmap(bitmap);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    editText.requestFocus();
                    Toast.makeText(QrActivity.this, "Please Enter Your Scanned Test" , Toast.LENGTH_LONG).show();
                }

            }
        });
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator integrator = new IntentIntegrator(QrActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();

            }
        });
    }
    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 350, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan");

            } else {
                Log.e("Scan", "Scanned");

                itemQRCode = result.getContents();
                Toast.makeText(this, "Scan Successfull" , Toast.LENGTH_SHORT).show();

                new GetItemNameClass().execute();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class GetItemNameClass extends AsyncTask<String,String,String> {

        private String itemNameForThis = null;


        @Override
        protected void onPreExecute(){

        }

        @Override
        protected String doInBackground(String... strings) {

            HttpJsonParser httpJsonParser1 = new HttpJsonParser();
            Map<String, String> httpParams1 = new HashMap<>();
            httpParams1.put("qr_code", itemQRCode);

            JSONObject jsonObject1 = httpJsonParser1.makeHttpRequest(
                    BASE_URL + "get_item_name_by_qr.php", "POST", httpParams1);
            try{

                int success1 = jsonObject1.getInt("success");

                JSONObject currentItem;


                if(success1 == 1){

                    currentItem = jsonObject1.getJSONObject("data");
                    itemNameForThis = currentItem.getString("item_Name");

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

                    Intent intent = new Intent(getApplicationContext(),CheckoutPopup.class
                    );
                    intent.putExtra("itemName",itemNameForThis);
                    intent.putExtra("itemQR", itemQRCode);
                    startActivityForResult(intent,20);
                }
            });
        }

    }
}