package com.example.sqltesting;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class HistoryUpdateActivity extends AppCompatActivity {
    private static String STRING_EMPTY = "";
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";
    private static final String KEY_HISTORY_ID = "historyID";
    private static final String KEY_DATE = "date";
    private static final String KEY_EVENT_NAME = "event";
    private static final String KEY_MOVIE_ID = "item_id";
    private static final String KEY_MOVIE_NAME = "item_name";
    private static final String KEY_JOB_NUMBER = "jobNumber";
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_EMP_NUMBER = "empNo";
    private static final String BASE_URL = "http://www.candyfactorylk.com/blog/movies/";
    private ArrayList<HashMap<String, String>> itemList;
    private String historyId;
    private EditText eventNameEditText;
    private EditText dateEditText;
    private EditText jobnumberEditText;
    private EditText departmentEditText;
    private EditText empnoEditText;
    private String eventName;
    private String date;
    private String jobNumber;
    private String department;
    private String empno;
    private Button deleteButton;
    private Button updateButton;
    private int success;
    private ProgressDialog pDialog;
    private ListView movieListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_update);
        Intent intent =getIntent();
        movieListView = (ListView) findViewById(R.id.historyItemList);

        eventNameEditText = (EditText) findViewById(R.id.txtevent);
        dateEditText = (EditText) findViewById(R.id.txtdate);
        jobnumberEditText = (EditText) findViewById(R.id.txtjobnumber);
        departmentEditText = (EditText) findViewById(R.id.txtdepartment);
        empnoEditText = (EditText) findViewById(R.id.txtempno);


        historyId = intent.getStringExtra(KEY_HISTORY_ID);


        new FetchHistoryDetailsAsyncTask().execute();

        new FetchSingleItem().execute();


    }


    private class FetchHistoryDetailsAsyncTask extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(HistoryUpdateActivity.this);
            pDialog.setMessage("Loading Event Details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String,String> httParams = new HashMap<>();
            httParams.put(KEY_HISTORY_ID,historyId);
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "get_history_details.php","GET",httParams);
            try{
                int success = jsonObject.getInt(KEY_SUCCESS);
                JSONObject history;
                if(success == 1){
                    history = jsonObject.getJSONObject(KEY_DATA);
                    eventName = history.getString(KEY_EVENT_NAME);
                    date = history.getString(KEY_DATE);
                    jobNumber = history.getString(KEY_JOB_NUMBER);
                    department = history.getString(KEY_DEPARTMENT);
                    empno = history.getString(KEY_EMP_NUMBER);

                }

            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String result){
            pDialog.dismiss();
            runOnUiThread(new Runnable() {

                public void run() {
                    eventNameEditText.setText(eventName);
                    dateEditText.setText(date);
                    jobnumberEditText.setText(jobNumber);
                    departmentEditText.setText(department);
                    empnoEditText.setText(empno);
                }
            });
        }

    }



    private class FetchSingleItem extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected String doInBackground(String... strings) {

            HttpJsonParser httpJsonParser1 = new HttpJsonParser();
            Map<String, String> httpParams1 = new HashMap<>();
            httpParams1.put("history_id", historyId);

            JSONObject jsonObject1 = httpJsonParser1.makeHttpRequest(
                    BASE_URL + "get_event_item_list.php", "POST", httpParams1);
            try {

                int success1 = jsonObject1.getInt(KEY_SUCCESS);

                JSONArray currentItems = null;

                if (success1 == 1) {

                    itemList = new ArrayList<>();
                    currentItems = jsonObject1.getJSONArray("data");

                    for (int i = 0; i < currentItems.length(); i++) {

                        JSONObject item = currentItems.getJSONObject(i);

                        Integer itemID = item.getInt("item_id");
                        String itemName = item.getString("item_name");

                        HashMap<String, String> map1 = new HashMap<String, String>();

                        map1.put(KEY_MOVIE_ID, itemID.toString());
                        map1.put(KEY_MOVIE_NAME, itemName.toString());

                        itemList.add(map1);

                    }
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String result){
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    populateMovieList();
                }
            });
        }

    }


    private void populateMovieList() {
        ListAdapter adapter = new SimpleAdapter(
                HistoryUpdateActivity.this, itemList,
                R.layout.list_item, new String[]{KEY_MOVIE_ID,
                KEY_MOVIE_NAME},
                new int[]{R.id.movieId, R.id.movieName});

        movieListView.setAdapter(adapter);


    }


}