package com.example.sqltesting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.example.sqltesting.helper.CheckNetworkStatus;
import com.example.sqltesting.helper.HttpJsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieListingActivity extends AppCompatActivity {
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";
    private static final String KEY_MOVIE_ID = "item_id";
    private static final String KEY_MOVIE_NAME = "item_name";
    private static final String KEY_MOVIE_IMAGE = "item_image";
    private static final String BASE_URL = "http://www.candyfactorylk.com/blog/movies/";
    private ArrayList<HashMap<String, String>> movieList;
    private ListView movieListView;
    private ProgressDialog pDialog;
    EditText editText;
    Button searchbtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_listing);
        movieListView = (ListView) findViewById(R.id.movieList);
        editText =(EditText)findViewById(R.id.search) ;
        searchbtn = (Button) findViewById(R.id.searchbtn);

        searchbtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    String searchKey =  editText.getText().toString();
                        new SearchItemActivity(searchKey).execute();
                    }
                }
        );




        new FetchMoviesAsyncTask().execute();



    }

    /**
     * Fetches the list of movies from the server
     */
    private class FetchMoviesAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display progress bar
            pDialog = new ProgressDialog(MovieListingActivity.this);
            pDialog.setMessage("Loading movies. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "fetch_all_movies.php", "GET", null);
            try {
                int success = jsonObject.getInt(KEY_SUCCESS);
                JSONArray movies;
                if (success == 1) {
                    movieList = new ArrayList<>();
                    movies = jsonObject.getJSONArray(KEY_DATA);
                    //Iterate through the response and populate movies list
                    for (int i = 0; i < movies.length(); i++) {
                        JSONObject movie = movies.getJSONObject(i);
                        Integer movieId = movie.getInt(KEY_MOVIE_ID);
                        String movieName = movie.getString(KEY_MOVIE_NAME);
                        String movieImage = movie.getString(KEY_MOVIE_IMAGE);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(KEY_MOVIE_ID, movieId.toString());
                        map.put(KEY_MOVIE_NAME, movieName);
                        map.put(KEY_MOVIE_IMAGE, movieImage);
                        movieList.add(map);
                    }


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
                    populateMovieList();
                }
            });
        }

    }

    private class SearchItemActivity extends AsyncTask<String, String, String> {

        private String searchKey = null;

        public SearchItemActivity(String SearchKey){

            this.searchKey = SearchKey;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display progress bar
            pDialog = new ProgressDialog(MovieListingActivity.this);
            pDialog.setMessage("Loading movies. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<String, String>();
            httpParams.put("SearchKey", this.searchKey);
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "search_all_movies.php", "POST", httpParams);
            try {
                int success = jsonObject.getInt(KEY_SUCCESS);
                JSONArray movies;
                if (success == 1) {
                    movieList = new ArrayList<>();
                    movies = jsonObject.getJSONArray(KEY_DATA);
                    //Iterate through the response and populate movies list
                    for (int i = 0; i < movies.length(); i++) {
                        JSONObject movie = movies.getJSONObject(i);
                        Integer movieId = movie.getInt(KEY_MOVIE_ID);
                        String movieName = movie.getString(KEY_MOVIE_NAME);
                        String movieImage = movie.getString(KEY_MOVIE_IMAGE);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(KEY_MOVIE_ID, movieId.toString());
                        map.put(KEY_MOVIE_NAME, movieName);
                        map.put(KEY_MOVIE_IMAGE, movieImage);
                        movieList.add(map);
                    }


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
                    populateMovieList();
                }
            });
        }

    }

    /**
     * Updating parsed JSON data into ListView
     * */
    private void populateMovieList() {
        ListAdapter adapter = new SimpleAdapter(
                MovieListingActivity.this, movieList,
                R.layout.list_item, new String[]{KEY_MOVIE_ID,
                KEY_MOVIE_NAME,KEY_MOVIE_IMAGE},
                new int[]{R.id.movieId, R.id.movieName, R.id.movieImage});


        // updating listview
        movieListView.setAdapter(adapter);
        //Call MovieUpdateDeleteActivity when a movie is clicked
        movieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Check for network connectivity
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    String movieId = ((TextView) view.findViewById(R.id.movieId))
                            .getText().toString();
                    Intent intent = new Intent(getApplicationContext(),
                            MovieUpdateDeleteActivity.class);
                    intent.putExtra(KEY_MOVIE_ID, movieId);
                    startActivityForResult(intent, 20);

                } else {
                    Toast.makeText(MovieListingActivity.this,
                            "Unable to connect to internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 20) {
            // If the result code is 20 that means that
            // the user has deleted/updated the movie.
            // So refresh the movie listing
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }



}
