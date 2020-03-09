package com.example.sqltesting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

public class historyActivity extends AppCompatActivity {
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";
    private static final String KEY_MOVIE_ID = "historyID";
    private static final String KEY_MOVIE_NAME = "event";
    private static final String BASE_URL = "http://www.candyfactorylk.com/blog/movies/";
    private ArrayList<HashMap<String, String>> historyList;
    private ListView historyListView;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyListView = (ListView) findViewById(R.id.historyList);
        new FetchHistorysAsyncTask().execute();
       

    }

    private class FetchHistorysAsyncTask extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog  = new ProgressDialog(historyActivity.this);
            pDialog.setMessage("Loading History");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "fetch_all_history.php", "GET", null);
            try {
                int success = jsonObject.getInt(KEY_SUCCESS);
                JSONArray historys;
                if (success == 1) {
                    historyList = new ArrayList<>();
                    historys = jsonObject.getJSONArray(KEY_DATA);
                    //Iterate through the response and populate historys list
                    for (int i = 0; i < historys.length(); i++) {
                        JSONObject history = historys.getJSONObject(i);
                        Integer movieId = history.getInt(KEY_MOVIE_ID);
                        String movieName = history.getString(KEY_MOVIE_NAME);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(KEY_MOVIE_ID, movieId.toString());
                        map.put(KEY_MOVIE_NAME, movieName);
                        historyList.add(map);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String result){
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    populateHistoryList();
                }
            });
        }
    }

    private  void populateHistoryList(){
        ListAdapter adapter = new SimpleAdapter(
                historyActivity.this,historyList,R.layout.list_item_image,new String[]{
                        KEY_MOVIE_ID,KEY_MOVIE_NAME},new int[]{R.id.movieId,R.id.movieName});
        historyListView.setAdapter(adapter);
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){


            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l ) {
                if(CheckNetworkStatus.isNetworkAvailable(getApplicationContext())){
                    String movieId = ((TextView) view.findViewById(R.id.movieId)).getText().toString();
                    Intent intent = new Intent(getApplicationContext(),HistoryUpdateActivity.class
                            );
                    intent.putExtra(KEY_MOVIE_ID,movieId);
                    startActivityForResult(intent,20);


                }
                else{
                    Toast.makeText(historyActivity.this,"unable to connect to internet",Toast.LENGTH_LONG).show();
                }

                }
        });


    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==20){
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }


}
