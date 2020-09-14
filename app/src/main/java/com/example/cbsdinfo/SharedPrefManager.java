package com.example.cbsdinfo;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager
{
    private static final String SHARED_PREF_NAME="fcmsharedpref";
    private static final String KEY_ACCESS_TOKEN="token";


    private static Context context;
    private static SharedPrefManager sharedPrefManager;

    private SharedPrefManager(Context ctx)
    {
        context=ctx;
    }

    public static synchronized SharedPrefManager getInstance(Context contexty)
    {
        if(sharedPrefManager==null)
        {
            sharedPrefManager=new SharedPrefManager(contexty);
        }
        return sharedPrefManager;
    }

    public boolean storeToken(String token)
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN,token);
        editor.apply();
        return true;
    }

    public String getToken()
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_TOKEN,null);
    }

}
