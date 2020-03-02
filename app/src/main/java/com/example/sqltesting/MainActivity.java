package com.example.sqltesting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import prefs.UserSession;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sqltesting.helper.CheckNetworkStatus;

public class MainActivity extends AppCompatActivity {
    private UserSession userSession;
    public static final int CAMERA_REQUEST = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        Button viewAllBtn = (Button) findViewById(R.id.viewAllBtn);
        Button addNewBtn = (Button) findViewById(R.id.addNewBtn);
        Button qr = (Button) findViewById(R.id.Qr);
        Button cart =(Button) findViewById(R.id.cart);
        Button profile = (Button) findViewById(R.id.profile);
        Button history = (Button) findViewById(R.id.hitory);
//        Button logout = (Button) findViewById(R.id.logout);


        /*implementing the buttons click events*/

        viewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check for network connectivity
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    Intent i = new Intent(getApplicationContext(),
                            MovieListingActivity.class);
                    startActivity(i);
                } else {
                    //Display error message if not connected to internet
                    Toast.makeText(MainActivity.this,
                            "Unable to connect to internet",
                            Toast.LENGTH_LONG).show();

                }

            }
        });

        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),QrActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),CartActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),Profilee.class);
                v.getContext().startActivity(intent);
            }
        });

        addNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check for network connectivity
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    Intent i = new Intent(getApplicationContext(),
                            AddMovieActivity.class);
                    startActivity(i);
                } else {
                    //Display error message if not connected to internet
                    Toast.makeText(MainActivity.this,
                            "Unable to connect to internet",
                            Toast.LENGTH_LONG).show();

                }

            }
        });

        history.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(CheckNetworkStatus.isNetworkAvailable(getApplicationContext())){
                    Intent i = new Intent(getApplicationContext(),
                            historyActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(MainActivity.this, "unable to connect to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }





/*camera open activity*/
//    public void OpenCamera(View view){
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE
//        );
//        startActivityForResult(intent,CAMERA_REQUEST);
//    }

}
