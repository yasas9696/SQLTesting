package com.example.sqltesting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class HistoryUpdateActivity extends AppCompatActivity {
    private static String STRING_EMPTY = "";
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";
    private static final String KEY_HISTORY_ID = "historyID";
    private static final String KEY_DATE = "date";
    private static final String KEY_EVENT_NAME = "event";

    private static final String KEY_JOB_NUMBER = "jobNumber";
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_EMP_NUMBER = "empNo";
    private static final String BASE_URL = "http://www.candyfactorylk.com/blog/movies/";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_update);
        Intent intent =getIntent();
        eventNameEditText = (EditText) findViewById(R.id.txtevent);
        dateEditText = (EditText) findViewById(R.id.txtdate);
        jobnumberEditText = (EditText) findViewById(R.id.txtjobnumber);
        departmentEditText = (EditText) findViewById(R.id.txtdepartment);
        empnoEditText = (EditText) findViewById(R.id.txtempno) ;


        historyId = intent.getStringExtra(KEY_HISTORY_ID);
        new FetchHistoryDetailsAsyncTask().execute();

        deleteButton = (Button) findViewById(R.id.btnDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete();
            }
        });
        updateButton = (Button) findViewById(R.id.btnUpdate);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckNetworkStatus.isNetworkAvailable(getApplicationContext())){
                    updateHistory();
                }
                else{
                    Toast.makeText(HistoryUpdateActivity.this , "Unable to connect to internet",Toast.LENGTH_LONG).show();
                }
            }
        });





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


    private void confirmDelete() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                HistoryUpdateActivity.this);
        alertDialogBuilder.setMessage("Are you sure, you want to delete this event?");
        alertDialogBuilder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                            //If the user confirms deletion, execute DeleteMovieAsyncTask
                            new DeleteHistoryAsyncTask().execute();
                        } else {
                            Toast.makeText(HistoryUpdateActivity.this,
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
    private class DeleteHistoryAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display progress bar
            pDialog = new ProgressDialog(HistoryUpdateActivity.this);
            pDialog.setMessage("Deleting Event. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            //Set movie_id parameter in request
            httpParams.put(KEY_HISTORY_ID, historyId);
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "delete_history.php", "POST", httpParams);
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
                        Toast.makeText(HistoryUpdateActivity.this,
                                "Movie Deleted", Toast.LENGTH_LONG).show();
                        Intent i = getIntent();
                        //send result code 20 to notify about movie deletion
                        setResult(20, i);
                        finish();

                    } else {
                        Toast.makeText(HistoryUpdateActivity.this,
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
    private void updateHistory() {


        if (!STRING_EMPTY.equals(eventNameEditText.getText().toString()) &&
                !STRING_EMPTY.equals(dateEditText.getText().toString()) &&
                !STRING_EMPTY.equals(jobnumberEditText.getText().toString()) &&
                !STRING_EMPTY.equals(departmentEditText.getText().toString()) &&
                !STRING_EMPTY.equals(empnoEditText.getText().toString())) {

            eventName = eventNameEditText.getText().toString();
            date = dateEditText.getText().toString();
            jobNumber = jobnumberEditText.getText().toString();
            department = departmentEditText.getText().toString();
            new UpdateHistoryAsyncTask().execute();
        } else {
            Toast.makeText(HistoryUpdateActivity.this,
                    "One or more fields left empty!",
                    Toast.LENGTH_LONG).show();

        }


    }
    /**
     * AsyncTask for updating a movie details
     */

    private class UpdateHistoryAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display progress bar
            pDialog = new ProgressDialog(HistoryUpdateActivity.this);
            pDialog.setMessage("Updating event. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            //Populating request parameters
            httpParams.put(KEY_HISTORY_ID, historyId);
            httpParams.put(KEY_EVENT_NAME, eventName);
            httpParams.put(KEY_DATE, date);
            httpParams.put(KEY_DEPARTMENT,department);
            httpParams.put(KEY_JOB_NUMBER, jobNumber);
            httpParams.put(KEY_EMP_NUMBER,empno);
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "update_history.php", "POST", httpParams);
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
                        Toast.makeText(HistoryUpdateActivity.this,
                                "event Updated", Toast.LENGTH_LONG).show();
                        Intent i = getIntent();
                        //send result code 20 to notify about movie update
                        setResult(20, i);
                        finish();

                    } else {
                        Toast.makeText(HistoryUpdateActivity.this,
                                "Some error occurred while updating movie",
                                Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
}

