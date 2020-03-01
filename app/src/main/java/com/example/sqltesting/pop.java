package com.example.sqltesting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class pop extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
//        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
//            finish();
//            startActivity(new Intent(this, MainActivity.class));
//            return;
//        }

        findViewById(R.id.sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if user pressed on login
                //we will open the login screen
                finish();
                startActivity(new Intent(pop.this, Login.class));
            }
        });

        SharedPreferences sharedPreferences = null;
        sharedPreferences = getSharedPreferences("sp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String cartList = sharedPreferences.getString("CartList", null);

        if(cartList == null){

            ArrayList<String> initialArrayList = new ArrayList<String>();
            Gson gson = new Gson();

            String json = gson.toJson(initialArrayList);

            editor.putString("CartList", json);

            editor.apply();

            Toast.makeText(this, "Cart Created Successfully", Toast.LENGTH_LONG).show();
        }
        else{

            Gson gson = new Gson();

            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<String> currentList = gson.fromJson(cartList, type);

            currentList.clear();

            String json = gson.toJson(currentList);

            editor.putString("CartList", json);

            editor.apply();

            Toast.makeText(this, "Cart Cleared Successfully", Toast.LENGTH_LONG).show();

        }

//        Button mButton =(Button) findViewById(R.id.sign);
//        mButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(pop.this, MainActivity.class));
//            }
//        });
    }


}
