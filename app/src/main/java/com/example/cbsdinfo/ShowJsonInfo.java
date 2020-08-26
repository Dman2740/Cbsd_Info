package com.example.cbsdinfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    String totalParse="";
    private Context context;
    DatabaseHelper databaseHelper;
    private ProgressDialog p;
    Boolean isUpdated=false;

    public ShowJsonInfo(Context context, DatabaseHelper db,DatabaseReference ref)
    {
        this.reference=ref;
        this.context=context;
        this.databaseHelper=db;
        this.p=new ProgressDialog(context);
    }
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        p.setMessage("Pulling Data From Federated");
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
            String singleParse="";
            URL url = new URL("https://spectrum-connect.federatedwireless.com:9998/v1.1/registration/cbsd?owner=&fccId=");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "Basic ZGFyaW9heWFsYTk4QGdtYWlsLmNvbTpDb2xvbWJpQDM2NQ==");
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
                String id=registrationValues.getString("cbsdId");
                JSONObject installationParamValues = registrationValues.getJSONObject("installationParam");
                JSONObject locationInfoValues = registrationValues.getJSONObject("locationInfo");
                JSONArray grantArray = jo.getJSONArray("grants");
                JSONObject grantValues = grantArray.getJSONObject(0);
                String flag=grantValues.getString("operationState");
                if(flag.equals("AUTHORIZED"))
                {
                    JSONObject operationFreqRangeValues = grantValues.getJSONObject("operationFrequencyRange");
                    singleParse = "ID:" + registrationValues.getString("cbsdId") + "\n" +
                            "Site:" + locationInfoValues.getString("countyName") + "\n" +
                            "Operation State:" + grantValues.getString("operationState") + "\n"+
                            "Latitude:" + installationParamValues.getDouble("latitude") + "\n" +
                            "Longitude:" + installationParamValues.getDouble("longitude") + "\n" +
                            "Grant ID:" + grantValues.getString("grantId") + "\n" +
                            "Low Frequency:"+operationFreqRangeValues.getString("lowFrequency")+"\n"+
                            "High Frequency:"+operationFreqRangeValues.getString("highFrequency")+"\n"+
                            "Max Eirp:" + grantValues.getDouble("maxEirp") + "\n" +
                            "Grant Timestamp:" + grantValues.getString("grantedTimestamp")+"\n"+"\n";
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
                    Query query=reference.child("CBSD Info").orderByChild("ID");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                //Data exists
                                isUpdated=true;
                            }
                            else
                            {
                                isUpdated=false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                    reference.child("CBSD Info").child(registrationValues.getString("cbsdId")).setValue(map);
                }
                else if(flag.equals("NONE"))
                {
                    singleParse ="ID:" + registrationValues.getString("cbsdId") + "\n" +
                            "Site:" + locationInfoValues.getString("countyName") + "\n" +
                            "Operation State:" + grantValues.getString("operationState") + "\n" +
                            "Latitude:" + installationParamValues.getDouble("latitude") + "\n" +
                            "Longitude:" + installationParamValues.getDouble("longitude") + "\n" +
                            "Grant ID:" + grantValues.optString("grantId") + "\n" +
                            "Low Frequency:" + 0 + "\n" +
                            "High Frequency:" + 0 + "\n"+
                            "Max Eirp:" + grantValues.optDouble("maxEirp") + "\n" +
                            "Grant Timestamp:" + grantValues.optString("grantedTimestamp") + "\n" + "\n";
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

                    Query query=reference.child("CBSD Info").orderByChild("ID");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                isUpdated=true;
                            }
                            else
                            {
                                isUpdated=false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });

                    reference.child("CBSD Info").child(registrationValues.getString("cbsdId")).setValue(map);
                }
                totalParse=totalParse+singleParse;
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
            MainActivity.data.setText(this.totalParse);
            if(isUpdated==false)
            {
                Toast.makeText(context, "No Updated Information", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(context, "There is Updated Information", Toast.LENGTH_LONG).show();
            }
        }
    }

}
