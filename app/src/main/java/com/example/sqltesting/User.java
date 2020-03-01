package com.example.sqltesting;

public class User {
    private int EmpID;
    private String EmpFname, EmpLname, uname, email, dept, type;

    public User(int EmpID, String EmpFname, String EmpLname, String uname, String email, String dept, String type) {
        this.EmpID = EmpID;
        this.EmpFname = EmpFname;
        this.EmpLname = EmpLname;
        this.uname = uname;
        this.email = email;
        this.dept = dept;
        this.type= type;
    }

    public int getEmpID() {
        return EmpID;
    }

    public String getEmpFname() {
        return EmpFname;
    }

    public String getEmpLname() {
        return EmpLname;
    }

    public String getUname() {
        return uname;
    }

    public String getEmail() {
        return email;
    }

    public String getDept() {
        return dept;
    }

    public String getType() {
        return type;
    }
}
