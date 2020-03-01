package prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class UserInfo {
    private static final String TAG = UserSession.class.getSimpleName();
    private static final String PREF_NAME = "userinfo";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_DEPT = "dept";
    private static final String KEY_TYPE = "type";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    public UserInfo(Context ctx) {
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences(PREF_NAME, ctx.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setUsername(String username){
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public void setEmail(String email){
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }
    public void setName(String name){
        editor.putString(KEY_NAME, name);
        editor.apply();
    }
    public void setDept(String dept){
        editor.putString(KEY_DEPT, dept);
        editor.apply();
    }
    public void setType(String type){
        editor.putString(KEY_TYPE, type);
        editor.apply();
    }

    public void clearUserInfo(){
        editor.clear();
        editor.commit();
    }

    public String getKeyUsername(){return prefs.getString(KEY_USERNAME, "");}

    public String getKeyEmail(){return prefs.getString(KEY_EMAIL, "");}
    public String getKeyName(){return prefs.getString(KEY_NAME, "");}
    public String getKeyDept(){return prefs.getString(KEY_DEPT, "");}
    public String getKeyType(){return prefs.getString(KEY_TYPE, "");}

}
