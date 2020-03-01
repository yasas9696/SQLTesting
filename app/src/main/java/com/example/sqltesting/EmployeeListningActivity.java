package com.example.sqltesting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class EmployeeListningActivity extends AppCompatActivity {
    private static final String KEY_SUCCESS ="success";
    private static final String KEY_DATA = "data";
    private static final String KEY_EMP_ID= "EmpID";
    private static final String KEY_EMP_FNAME ="EmpFname";
    private static final String KEY_EMP_LNAME = "EmpLname";
//    private static final String KEY_PASSWORD  = "password";
    private static final String KEY_DEPARTMENT = "dept";
    private static final String KEY_TYPE ="type";

    private static final String BASE_URL ="http://www.candyfactorylk.com/blog/movies/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_listning);
    }


}
