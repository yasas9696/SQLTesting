package com.example.sqltesting;

import androidx.appcompat.app.AppCompatActivity;
import prefs.UserInfo;
import prefs.UserSession;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Profilee extends AppCompatActivity {

    private Button logout;
    private TextView tvUsername, tvEmail, tvName, tvDept, tvType;
    private UserInfo userInfo;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilee);

        userInfo        = new UserInfo(this);
        userSession     = new UserSession(this);
        logout          = (Button)findViewById(R.id.logout);

        tvUsername      = (TextView)findViewById(R.id.key_username);
        tvEmail         = (TextView)findViewById(R.id.key_email);
        tvName = (TextView)findViewById(R.id.key_name);
        tvDept = (TextView)findViewById(R.id.key_dept);
        tvType = (TextView)findViewById(R.id.key_type);

        if(!userSession.isUserLoggedin()){
            startActivity(new Intent(this, Login.class));
            finish();
        }

        String username = userInfo.getKeyUsername();
        String email    = userInfo.getKeyEmail();
        String name = userInfo.getKeyName();
        String dept = userInfo.getKeyDept();
        String type = userInfo.getKeyType();

        tvUsername.setText(username);
        tvEmail.setText(email);
        tvName.setText(name);
        tvDept.setText(dept);
        tvType.setText(type);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSession.setLoggedin(false);
                userInfo.clearUserInfo();



                Intent intent = new Intent(Profilee.this, Login.class);
                intent.putExtra("finish", true); // if you are checking for this in your other Activities
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });


    }

}
