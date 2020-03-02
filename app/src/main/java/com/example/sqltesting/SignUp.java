package com.example.sqltesting;

import androidx.appcompat.app.AppCompatActivity;
import prefs.UserInfo;
import prefs.UserSession;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    private String TAG = SignUp.class.getSimpleName();
    private EditText username, email, password, name, dept, type;
    private Button signup;
    private ProgressDialog progressDialog;
    private UserSession session;
    private UserInfo userInfo;
    private static String STRING_EMPTY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username        = (EditText)findViewById(R.id.username);
        email           = (EditText)findViewById(R.id.email);
        password        = (EditText)findViewById(R.id.password);
        name        = (EditText)findViewById(R.id.name);
        dept        = (EditText)findViewById(R.id.dept);
        type        = (EditText)findViewById(R.id.type);

        signup          = (Button)findViewById(R.id.signup);
        progressDialog  = new ProgressDialog(this);
        session         = new UserSession(this);
        userInfo        = new UserInfo(this);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!STRING_EMPTY.equals(username.getText().toString()) &&
                        !STRING_EMPTY.equals(email.getText().toString()) &&
                        !STRING_EMPTY.equals(password.getText().toString()) &&
                        !STRING_EMPTY.equals(name.getText().toString())) {


                    String uName = username.getText().toString().trim();
                    String mail = email.getText().toString().trim();
                    String pass = password.getText().toString().trim();
                    String nam = name.getText().toString().trim();
                    String dep = dept.getText().toString().trim();
                    String typ = type.getText().toString().trim();

                    signup(uName, mail, pass, nam, dep, typ);
                }else{
                    toast("Fields cannot be empty");
                }
            }
        });
    }

    private void signup(final String username, final String email, final String password, final String name, final String dept, final String type){
        // Tag used to cancel the request
        String tag_string_req = "req_signup";
        progressDialog.setMessage("Signing up...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Utils.REGISTER_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        JSONObject user = jObj.getJSONObject("user");
                        String uName = user.getString("username");
                        String email = user.getString("email");
                        String nam = user.getString("name");
                        String dep = user.getString("dept");
                        String typ = user.getString("type");

                        // Inserting row in users table
                        userInfo.setEmail(email);
                        userInfo.setUsername(uName);
                        userInfo.setName(nam);
                        userInfo.setDept(dep);
                        userInfo.setType(typ);
                        session.setLoggedin(true);

                        startActivity(new Intent(SignUp.this, MainActivity.class));
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        toast(errorMsg);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    toast("Json error: " + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                toast("Unknown Error occurred");
                progressDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                params.put("name", name);
                params.put("dept", dept);
                params.put("type", type);

                return params;
            }

        };

        // Adding request to request queue
        AndroidLoginController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void toast(String x){
        Toast.makeText(this, x, Toast.LENGTH_SHORT).show();
    }
}
