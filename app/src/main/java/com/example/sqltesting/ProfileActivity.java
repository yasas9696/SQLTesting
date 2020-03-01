//package com.example.sqltesting;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.TextView;
//
//public class ProfileActivity extends AppCompatActivity {
//    TextView textViewId, textViewUsername, textViewEmail, textViewGender, textViewFname, textViewType,textViewLname;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile);
//        //if the user is not logged in
//        //starting the login activity
//
//
//
//        textViewId = (TextView) findViewById(R.id.textViewId);
//        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
//        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
//        textViewGender = (TextView) findViewById(R.id.textViewGender);
//        textViewFname = (TextView) findViewById(R.id.textViewFname) ;
//        textViewLname = (TextView) findViewById(R.id.textViewLname);
//        textViewType = (TextView) findViewById(R.id.textViewType);
//
//
//        //getting the current user
//        User user = SharedPrefManager.getInstance(this).getUser();
//
//        //setting the values to the textviews
//        textViewId.setText(String.valueOf(user.getEmpID()));
//        textViewFname.setText(user.getEmpFname());
//        textViewLname.setText(user.getEmpLname());
//        textViewUsername.setText(user.getUname());
//        textViewEmail.setText(user.getEmail());
//        textViewGender.setText(user.getDept());
//        textViewType.setText(user.getType());
//
//        //when the user presses logout button
//        //calling the logout method
//        findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//                SharedPrefManager.getInstance(getApplicationContext()).logout();
//            }
//        });
//    }
//}