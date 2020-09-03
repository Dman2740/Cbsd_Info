package com.example.cbsdinfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckCredentials extends AsyncTask<Void,Void,Boolean>
{
    private ProgressDialog p;
    Boolean checkCredential;
    private Context contexty;
    DatabaseReference reference;
    String userId="";
    String authy="";

    public CheckCredentials(Context context, DatabaseReference ref, String auth)
    {
        this.reference=ref;
        this.authy=auth;
        this.contexty=context;
        this.p=new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        p.setMessage("Checking Credentials");
        p.setIndeterminate(false);
        p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        p.setCancelable(false);
        p.show();
    }


    @Override
    protected Boolean doInBackground(Void... strings)
    {
        try
        {
            String data="";
            URL url = new URL("https://spectrum-connect.federatedwireless.com:9998/v1.1/registration/cbsd?owner=&fccId=");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", authy);
            int response=urlConnection.getResponseCode();
            if(response==HttpURLConnection.HTTP_OK)
            {
                checkCredential=true;
            }
            else
            {
                checkCredential=false;
            }
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader bin = new BufferedReader(new InputStreamReader(in));
            String inputLine = "";
            while((inputLine=bin.readLine()) != null)
            {
                data = data + inputLine;
            }
            in.close();
            String crappyPrefix = "null";
            if (data.startsWith(crappyPrefix))
            {
                data = data.substring(crappyPrefix.length(), data.length() - crappyPrefix.length());
            }
            String newData=data;
            JSONArray ja = new JSONArray(newData);
            JSONObject jo = ja.getJSONObject(0);
            JSONObject registrationValues = jo.getJSONObject("registration");
            userId=registrationValues.getString("userId");


            return checkCredential;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Boolean result)
    {
        super.onPostExecute(result);
        p.dismiss();
        if(checkCredential==true)
        {
            Intent intent = new Intent(contexty, MainActivity.class);
            intent.putExtra("key", authy);
            intent.putExtra("userId",userId);
            contexty.startActivity(intent);
        }
        else
        {
            Toast.makeText(contexty,"Credentials Were Not Valid",Toast.LENGTH_LONG).show();
        }

    }
}
