package com.example.sqltesting;


import androidx.appcompat.app.AppCompatActivity;
import prefs.UserInfo;
import prefs.UserSession;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sqltesting.helper.CheckNetworkStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class CartActivity extends AppCompatActivity {
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_MOVIE_ID = "cartID";
    private static final String KEY_CART_ITEM_ID = "cartItemID";
    private static final String KEY_CART_ITEM_NAME =  "cartItemName";
    private static final String BASE_URL = "http://www.candyfactorylk.com/blog/movies/";

    private UserInfo userInfo;
    private UserSession userSession;
    private ArrayList<HashMap<String, String>> itemList;

    private EditText jobNumber;
    private EditText eventName;
    private EditText department;
    private EditText empName;
    private Button checkoutAllButton;
    private ListView itemListView;

    private ProgressDialog pDialog;
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        queue = Volley.newRequestQueue(this);

        jobNumber = (EditText) findViewById(R.id.txtJobNumber);
        eventName = (EditText) findViewById(R.id.txtEventName);
        department = (EditText) findViewById(R.id.txtDepartment);
        empName = (EditText) findViewById(R.id.txtEmpName);
        itemListView = (ListView) findViewById(R.id.itemlist);
        checkoutAllButton = (Button) findViewById(R.id.checkoutAllButton);

        sharedPreferences = getSharedPreferences("sp" , Context.MODE_PRIVATE);

        userInfo = new UserInfo(this);
        userSession = new UserSession(this);

        String name = userInfo.getKeyName();
        empName.setText(name);

        new FetchMoviesAsyncTask().execute();

        checkoutAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkoutItems();
            }
        });
    }

    protected void checkoutItems(){
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("JobNumber", jobNumber.getText());
            jsonObject.put("EventName", eventName.getText());
            jsonObject.put("Department", department.getText());
            jsonObject.put("EmpName", empName.getText());

            JSONArray checkoutList = new JSONArray();

            for (int i=0; i < itemList.size(); i++){
                HashMap<String, String> currentItem = itemList.get(i);

                String itemID1 = currentItem.get(KEY_CART_ITEM_ID);
                checkoutList.put(itemID1);
            }

            jsonObject.put("ItemCollection", checkoutList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = jsonObject.toString();
        String url = BASE_URL + "checkout_all_items.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        // Log.d("Error.Response", response);
                    }
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

        };

        queue.add(postRequest);

        Toast.makeText(CartActivity.this, "All items checked out successfully.", Toast.LENGTH_LONG).show();

    }

    /**
     * Fetches the list of movies from the server
     */
    private class FetchMoviesAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Display progress bar
            pDialog = new ProgressDialog(CartActivity.this);
            pDialog.setMessage("Loading items. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            itemList = new ArrayList<>();

            // SharedPreferences.Editor editor = sharedPreferences.edit();

            String cartList = sharedPreferences.getString("CartList", null);

            Gson gson = new Gson();

            Type type = new TypeToken<ArrayList<HashMap<String, String>>>() {}.getType();
            itemList = gson.fromJson(cartList, type);

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

    private void populateMovieList() {
        ListAdapter adapter = new SimpleAdapter(
                CartActivity.this,
                itemList,
                R.layout.list_item_cart,
                new String[]{KEY_CART_ITEM_ID, KEY_CART_ITEM_NAME},
                new int[]{R.id.movieId, R.id.movieName}
        );

        itemListView.setAdapter(adapter);

        //Call MovieUpdateDeleteActivity when a movie is clicked
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Check for network connectivity
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    String movieId = ((TextView) view.findViewById(R.id.movieId)).getText().toString();

                    Intent intent = new Intent(getApplicationContext(), RmoveCartItemActivity.class);
                    intent.putExtra(KEY_MOVIE_ID, movieId);
                    startActivityForResult(intent, 20);
                } else {
                    Toast.makeText(CartActivity.this, "Unable to connect to internet", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
