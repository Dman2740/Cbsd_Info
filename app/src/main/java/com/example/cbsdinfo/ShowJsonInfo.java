package com.example.cbsdinfo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class ShowJsonInfo extends AsyncTask<String, String, HashMap<String, Object>>
{
    HashMap<String,Object> map=new HashMap<>();

    DatabaseReference reference;
    String authy="";
    String email="";
    String userId="";
    private Context context;
    private ProgressDialog p;
    Boolean checkCredential=false;

    public ShowJsonInfo(Context context,DatabaseReference ref,String auth,String email,String userId)
    {
        this.email=email;
        this.reference=ref;
        this.context=context;
        this.authy=auth;
        this.userId=userId;
        this.p=new ProgressDialog(context);
    }
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        p.setMessage("Sending Data to Database");
        p.setIndeterminate(false);
        p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        p.setCancelable(false);
        p.show();
    }
    @Override
    protected HashMap<String, Object> doInBackground(String... params)
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
            //ZGFyaW9heWFsYTk4QGdtYWlsLmNvbTpDb2xvbWJpQDM2NQ== this is mine
            //bWFydGluYWFib2plbnNlbkBnbWFpbC5jb206Sm9obldheW5lIzIx this is martins
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
            for (int i = 0; i < ja.length(); i++)
            {
                JSONObject jo = ja.getJSONObject(i);
                JSONObject registrationValues = jo.getJSONObject("registration");
                JSONObject installationParamValues = registrationValues.getJSONObject("installationParam");
                JSONObject locationInfoValues = registrationValues.getJSONObject("locationInfo");
                JSONArray grantArray = jo.getJSONArray("grants");
                JSONObject grantValues = grantArray.getJSONObject(0);
                String flag=grantValues.getString("operationState");
                if(flag.equals("AUTHORIZED"))
                {
                    JSONObject operationFreqRangeValues = grantValues.getJSONObject("operationFrequencyRange");
                    map.put("ID",registrationValues.getString("cbsdId"));
                    map.put("Site",locationInfoValues.getString("countyName"));
                    map.put("Operation State",grantValues.getString("operationState"));
                    map.put("Latitude",installationParamValues.getDouble("latitude"));
                    map.put("Longitude",installationParamValues.getDouble("longitude"));
                    map.put("Grant ID",grantValues.getString("grantId"));
                    map.put("Low Frequency",operationFreqRangeValues.getString("lowFrequency"));
                    map.put("High Frequency",operationFreqRangeValues.getString("highFrequency"));
                    map.put("Max Eirp",grantValues.getDouble("maxEirp"));
                    map.put("Grant Timestamp", grantValues.getString("grantedTimestamp"));
                    reference.child(registrationValues.getString("userId")).child("CBSD Info").child(registrationValues.getString("cbsdId")).setValue(map);
                }
                else if(flag.equals("NONE"))
                {
                    map.put("ID",registrationValues.getString("cbsdId"));
                    map.put("Site",locationInfoValues.getString("countyName"));
                    map.put("Operation State",grantValues.getString("operationState"));
                    map.put("Latitude",installationParamValues.getDouble("latitude"));
                    map.put("Longitude",installationParamValues.getDouble("longitude"));
                    map.put("Grant ID","");
                    map.put("Low Frequency","0");
                    map.put("High Frequency","0");
                    map.put("Max Eirp","0");
                    map.put("Grant Timestamp","");
                    reference.child(registrationValues.getString("userId")).child("CBSD Info").child(registrationValues.getString("cbsdId")).setValue(map);
                }
            }
            return map;
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPostExecute(HashMap<String, Object> response)//String response
    {
        if (response != null)
        {
            super.onPostExecute(response);
            p.dismiss();
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("userId",userId);
            context.startActivity(intent);
        }
    }

}
