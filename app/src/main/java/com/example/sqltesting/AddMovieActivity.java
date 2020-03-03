package com.example.sqltesting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.sqltesting.helper.CheckNetworkStatus;
import com.example.sqltesting.helper.HttpJsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddMovieActivity extends AppCompatActivity {
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_MOVIE_NAME = "item_name";
    private static final String KEY_GENRE = "item_model";
    private static final String KEY_YEAR = "item_qr";
    private static final String KEY_RATING = "item_status";
    private static final String KEY_TYPE = "item_type";
    private static final String KEY_CAPACITY = "item_capacity";

    private static final String BASE_URL = "http://www.candyfactorylk.com/blog/movies/";
    private static String STRING_EMPTY = "";
    private EditText movieNameEditText;
    private EditText genreEditText;
    private EditText yearEditText;
    private EditText ratingEditText;
    private EditText typeEditText;
    private EditText capacityEditText;


    private String movieName;
    private String genre;
    private String year;
    private String rating;
    private String type;
    private String capacity;


    private Button addButton;
    private int success;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);
        movieNameEditText = (EditText) findViewById(R.id.txtMovieNameAdd);
        genreEditText = (EditText) findViewById(R.id.txtGenreAdd);
        yearEditText = (EditText) findViewById(R.id.txtYearAdd);
        ratingEditText = (EditText) findViewById(R.id.txtRatingAdd);
        typeEditText = (EditText) findViewById(R.id.txtMovieTypeAdd) ;
        capacityEditText=(EditText) findViewById(R.id.txtMovieCapacityAdd) ;


        addButton = (Button) findViewById(R.id.checkoutAllButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    addMovie();
                } else {
                    Toast.makeText(AddMovieActivity.this,
                            "Unable to connect to internet",
                            Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    /**
     * Checks whether all files are filled. If so then calls AddMovieAsyncTask.
     * Otherwise displays Toast message informing one or more fields left empty
     */
    private void addMovie() {
        if (!STRING_EMPTY.equals(movieNameEditText.getText().toString()) &&
                !STRING_EMPTY.equals(genreEditText.getText().toString()) &&
                !STRING_EMPTY.equals(yearEditText.getText().toString()) &&
                !STRING_EMPTY.equals(ratingEditText.getText().toString())) {

            movieName = movieNameEditText.getText().toString();
            genre = genreEditText.getText().toString();
            year = yearEditText.getText().toString();
            rating = ratingEditText.getText().toString();
            capacity = capacityEditText.getText().toString();
            type = typeEditText.getText().toString();
            new AddMovieAsyncTask().execute();
        } else {
            Toast.makeText(AddMovieActivity.this,
                    "One or more fields left empty!",
                    Toast.LENGTH_LONG).show();

        }


    }

    /**
     * AsyncTask for adding a movie
     */
    private class AddMovieAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display proggress bar
            pDialog = new ProgressDialog(AddMovieActivity.this);
            pDialog.setMessage("Adding Movie. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            //Populating request parameters
            httpParams.put(KEY_MOVIE_NAME, movieName);
            httpParams.put(KEY_GENRE, genre);
            httpParams.put(KEY_YEAR, year);
            httpParams.put(KEY_RATING, rating);
            httpParams.put(KEY_CAPACITY, capacity);
            httpParams.put(KEY_TYPE, type);


            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "add_movie.php", "POST", httpParams);
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
                        Toast.makeText(AddMovieActivity.this,
                                "Movie Added", Toast.LENGTH_LONG).show();
                        Intent i = getIntent();
                        //send result code 20 to notify about movie update
                        setResult(20, i);
                        //Finish ths activity and go back to listing activity
                        finish();

                    } else {
                        Toast.makeText(AddMovieActivity.this,
                                "Some error occurred while adding movie",
                                Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
}