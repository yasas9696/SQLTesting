package com.example.sqltesting;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;


import com.example.sqltesting.helper.CheckNetworkStatus;
import com.example.sqltesting.helper.HttpJsonParser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieUpdateDeleteActivity extends AppCompatActivity {

    private static String STRING_EMPTY = "";
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";
    private static final String KEY_MOVIE_ID = "id";
    private static final String KEY_ITEM_TYPE = "item_type";
    private static final String KEY_MOVIE_NAME = "item_name";
    private static final String KEY_MODEL = "item_model";
    private static final String KEY_QR = "item_qr";
    private static final String KEY_STATUS = "item_status";
    private static final String KEY_CAPACITY = "item_capacity";
    private static final String KEY_IMAGE = "item_image";

    private static final String BASE_URL = "http://www.candyfactorylk.com/blog/movies/";
    private String itemId;
    private EditText itemTypeEditText;
    private ImageView movieImage;

    private EditText movieNameEditText;
    private EditText movieModelEditText;
    private EditText qrEditText;
    private EditText statusEditText;
    private EditText capacityEditText;



    private String itemName;
    private String itemModel;
    private String itemQr;
    private String itemQRFromCm;
    private String itemStatus;
    private String itemCapacity;
    private String itemImage;

    private String itemType;
    private Button deleteButton;
    private Button updateButton;
    private Button scn;
    private int success;
    private ProgressDialog pDialog;
    TextView tv_qr_readTxt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_update_delete);
        Intent intent = getIntent();

        movieImage = (ImageView) findViewById(R.id.image) ;
        itemTypeEditText = (EditText) findViewById(R.id.txtType);
        movieNameEditText = (EditText) findViewById(R.id.txtMovieNameUpdate);
        movieModelEditText = (EditText) findViewById(R.id.txtGenreUpdate);
        qrEditText = (EditText) findViewById(R.id.txtYearUpdate);
        statusEditText = (EditText) findViewById(R.id.txtRatingUpdate);
        capacityEditText = (EditText) findViewById(R.id.txtCapacity);
        tv_qr_readTxt = (TextView) findViewById(R.id.tv_qr_readTxt);
        itemId = intent.getStringExtra(KEY_MOVIE_ID);
        new FetchMovieDetailsAsyncTask().execute();
        deleteButton = (Button) findViewById(R.id.btnDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDelete();
            }
        });

        scn = (Button) findViewById(R.id.scanQR) ;
        scn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator integrator = new IntentIntegrator(MovieUpdateDeleteActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
        updateButton = (Button) findViewById(R.id.btnUpdate);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    updateMovie();

                } else {
                    Toast.makeText(MovieUpdateDeleteActivity.this,
                            "Unable to connect to internet",
                            Toast.LENGTH_LONG).show();

                }

            }
        });


    }

    /**
     * Fetches single movie details from the server
     */
    private class FetchMovieDetailsAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display progress bar
            pDialog = new ProgressDialog(MovieUpdateDeleteActivity.this);
            pDialog.setMessage("Loading Movie Details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            httpParams.put(KEY_MOVIE_ID, itemId);
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "get_movie_details.php", "GET", httpParams);
            try {
                int success = jsonObject.getInt(KEY_SUCCESS);
                JSONObject movie;
                if (success == 1) {
                    //Parse the JSON response
                    movie = jsonObject.getJSONObject(KEY_DATA);
                    itemType = movie.getString(KEY_ITEM_TYPE);
                    itemName = movie.getString(KEY_MOVIE_NAME);
                    itemModel = movie.getString(KEY_MODEL);
                    itemQr = movie.getString(KEY_QR);
                    itemStatus = movie.getString(KEY_STATUS);
                    itemImage  = movie.getString(KEY_IMAGE);
                    itemCapacity = movie.getString(KEY_CAPACITY);





                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    //Populate the Edit Texts once the network activity is finished executing
                    itemTypeEditText.setText(itemType);
                    movieNameEditText.setText(itemName);
                    movieModelEditText.setText(itemModel);
                    qrEditText.setText(itemQr);
                    statusEditText.setText(itemStatus);
                    capacityEditText.setText(itemCapacity);



                    String url = "https://lh3.googleusercontent.com/proxy/qUKduZIr1aCIkUSv4Jx8PLAUl9i15eAJ2XF70jd4qih0eIr-tVVmj3X-c47RxTDXvUcRADiOCDoOLKIjfrDxGhuNxL196S9dA5vDIn1DaJAKOfmvx5WbF0WTofbb7xRhU4W0tjRR";
                    Picasso.get().load(url).into(movieImage);


                }
            });
        }


    }



    /**
     * Displays an alert dialogue to confirm the deletion
     */
    private void confirmDelete() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MovieUpdateDeleteActivity.this);
        alertDialogBuilder.setMessage("Are you sure, you want to delete this Item?");
        alertDialogBuilder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                            //If the user confirms deletion, execute DeleteMovieAsyncTask
                            new DeleteMovieAsyncTask().execute();
                        } else {
                            Toast.makeText(MovieUpdateDeleteActivity.this,
                                    "Unable to connect to internet",
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * AsyncTask to delete a movie
     */
    private class DeleteMovieAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display progress bar
            pDialog = new ProgressDialog(MovieUpdateDeleteActivity.this);
            pDialog.setMessage("Deleting Movie. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            //Set movie_id parameter in request
            httpParams.put(KEY_MOVIE_ID, itemId);
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "delete_movie.php", "POST", httpParams);
            try {
                success = jsonObject.getInt(KEY_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    if (success == 1) {
                        //Display success message
                        Toast.makeText(MovieUpdateDeleteActivity.this,
                                "Movie Deleted", Toast.LENGTH_LONG).show();
                        Intent i = getIntent();
                        //send result code 20 to notify about movie deletion
                        setResult(20, i);
                        finish();

                    } else {
                        Toast.makeText(MovieUpdateDeleteActivity.this,
                                "Some error occurred while deleting movie",
                                Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }

    /**
     * Checks whether all files are filled. If so then calls UpdateMovieAsyncTask.
     * Otherwise displays Toast message informing one or more fields left empty
     */
    private void updateMovie() {


        if (!STRING_EMPTY.equals(movieNameEditText.getText().toString()) &&
                !STRING_EMPTY.equals(itemTypeEditText.getText().toString())
                &&
                !STRING_EMPTY.equals(qrEditText.getText().toString()) &&
                !STRING_EMPTY.equals(statusEditText.getText().toString()) &&
                !STRING_EMPTY.equals(capacityEditText.getText().toString())&&
                !STRING_EMPTY.equals(movieModelEditText.getText().toString())) {

            itemType = itemTypeEditText.getText().toString();
            itemName = movieNameEditText.getText().toString();
            itemModel = movieModelEditText.getText().toString();
            itemQr = qrEditText.getText().toString();
            itemStatus = statusEditText.getText().toString();
            itemCapacity = capacityEditText.getText().toString();

            new UpdateMovieAsyncTask().execute();
        } else {
            Toast.makeText(MovieUpdateDeleteActivity.this,
                    "One or more fields left empty!",
                    Toast.LENGTH_LONG).show();

        }


    }
    /**
     * AsyncTask for updating a movie details
     */

    private class UpdateMovieAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display progress bar
            pDialog = new ProgressDialog(MovieUpdateDeleteActivity.this);
            pDialog.setMessage("Updating Movie. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            //Populating request parameters
            httpParams.put(KEY_MOVIE_ID, itemId);
            httpParams.put(KEY_ITEM_TYPE, itemType);
            httpParams.put(KEY_MOVIE_NAME, itemName);
            httpParams.put(KEY_MODEL, itemModel);
            httpParams.put(KEY_QR, itemQr);
            httpParams.put(KEY_STATUS, itemStatus);
            httpParams.put(KEY_CAPACITY, itemCapacity);

            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "update_movie.php", "POST", httpParams);
            try {
                success = jsonObject.getInt(KEY_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    if (success == 1) {
                        //Display success message
                        Toast.makeText(MovieUpdateDeleteActivity.this,
                                "Movie Updated", Toast.LENGTH_LONG).show();
                        Intent i = getIntent();
                        //send result code 20 to notify about movie update
                        setResult(20, i);
                        finish();

                    } else {
                        Toast.makeText(MovieUpdateDeleteActivity.this,
                                "Some error occurred while updating movie",
                                Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan");

            } else {
                Log.e("Scan", "Scanned");

                itemQRFromCm = result.getContents();

                if(itemQr.equals(itemQRFromCm)){

                    try{
                        Toast.makeText(this, "Item matched" , Toast.LENGTH_LONG).show();

                        SharedPreferences sharedPreferences = getSharedPreferences("sp", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        String cartList = sharedPreferences.getString("CartList", null);

                        Gson gson = new Gson();

                        Type type = new TypeToken<ArrayList<HashMap<String, String>>>() {}.getType();
                        ArrayList<HashMap<String, String>> currentCartList = gson.fromJson(cartList, type);

                        //Create a new hashmap which includes itemID and itemName
                        HashMap<String, String> thisItem = new HashMap<String, String>();

                        thisItem.put("cartItemID", itemId);
                        thisItem.put("cartItemName", itemName);

                        currentCartList.add(thisItem);

                        String json = gson.toJson(currentCartList);

                        editor.putString("CartList", json);

                        editor.apply();

                        Toast.makeText(this, "Added to cart successfully", Toast.LENGTH_LONG).show();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                }
                else{
                    Toast.makeText(this, "QR Code Does Not Match", Toast.LENGTH_LONG).show();
                }

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}