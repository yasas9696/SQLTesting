package com.example.sqltesting;

import androidx.appcompat.app.AppCompatActivity;
import prefs.UserInfo;
import prefs.UserSession;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";
    private static final String KEY_MOVIE_ID = "cartID";
    private static final String KEY_MOVIE_NAME = "itemName";
    private static final String KEY_CART_ITEM_ID = "cartItemID";
    private static final String KEY_CART_ITEM_NAME =  "cartItemName";
    private static final String BASE_URL = "http://www.candyfactorylk.com/blog/movies/";
    private ArrayList<HashMap<String, String>> itemList;
    private ListView itemListView;
    private Button deleteButton;
    private Button checkoutAllButton = null;
    private String itemId;
    int success;
    private TextView tvName;
    private UserInfo userInfo;
    private UserSession userSession;



    private EditText jobnumber;
    private EditText eventname;
    private EditText department;
    private EditText empname;


    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        jobnumber =(EditText)findViewById(R.id.txtJobNumber) ;
        eventname = (EditText) findViewById(R.id.txtEventName);
        department =(EditText)findViewById(R.id.txtDepartment) ;
        empname = (EditText) findViewById(R.id.txtEmpName) ;





        userInfo = new UserInfo(this);
        userSession = new UserSession(this);

        tvName = (EditText)findViewById(R.id.txtEmpName) ;

        String name = userInfo.getKeyName();
        tvName.setText(name);


        itemListView = (ListView) findViewById(R.id.itemlist);
        new FetchMoviesAsyncTask().execute();

        SharedPreferences sharedPreferences = getSharedPreferences("sp" , Context.MODE_PRIVATE);
        String s1 = sharedPreferences.getString("CartList",null);

        checkoutAllButton = (Button) findViewById(R.id.checkoutAllButton);

        checkoutAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkoutItems();

            }
        });



    }

    protected void checkoutItems(){

        for (int i=0; i < itemList.size(); i++){

            HashMap<String, String> currentItem = itemList.get(i);

            String itemID1 = currentItem.get(KEY_CART_ITEM_ID);
            String itemName1 = currentItem.get(KEY_CART_ITEM_NAME);


            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            //Populating request parameters
            httpParams.put(KEY_CART_ITEM_ID, itemID1);
            httpParams.put(KEY_CART_ITEM_NAME, itemName1);

            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "checkout_all_items.php", "POST", httpParams);
            try {


                success = jsonObject.getInt(KEY_SUCCESS);
                System.out.println(success);

                if (success != 1){

                    Toast.makeText(CartActivity.this, "Error occured while checking out items.", Toast.LENGTH_LONG).show();
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

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

            SharedPreferences sharedPreferences = getSharedPreferences("sp", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

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
                CartActivity.this, itemList,
                R.layout.list_item_cart, new String[]{KEY_CART_ITEM_ID,
                KEY_CART_ITEM_NAME},
                new int[]{R.id.movieId, R.id.movieName});
        itemListView.setAdapter(adapter);

        //Call MovieUpdateDeleteActivity when a movie is clicked
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Check for network connectivity
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    String movieId = ((TextView) view.findViewById(R.id.movieId))
                            .getText().toString();
                    Intent intent = new Intent(getApplicationContext(),
                            RmoveCartItemActivity.class);
                    intent.putExtra(KEY_MOVIE_ID, movieId);
                    startActivityForResult(intent, 20);

                } else {
                    Toast.makeText(CartActivity.this,
                            "Unable to connect to internet",
                            Toast.LENGTH_LONG).show();

                }


            }
        });
    }


}
