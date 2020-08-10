package com.example.cbsdinfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

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

public class ShowJsonInfo extends AsyncTask<String,Void,String>
{
    String totalParse="";
    private Context context;
    DatabaseHelper databaseHelper;
    private ProgressDialog p;
    Boolean isUpdated=false;

    public ShowJsonInfo(Context context, DatabaseHelper db)
    {
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
    protected String doInBackground(String... params)
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
                    if(databaseHelper.noDuplicate(id))
                    {
                        boolean isInserted =
                                databaseHelper.insertData(
                                registrationValues.getString("cbsdId"),
                                locationInfoValues.getString("countyName"),
                                grantValues.getString("operationState"),
                                installationParamValues.getDouble("latitude"),
                                installationParamValues.getDouble("longitude"),
                                grantValues.getString("grantId"),
                                operationFreqRangeValues.getString("lowFrequency"),
                                operationFreqRangeValues.getString("highFrequency"),
                                grantValues.getDouble("maxEirp"),
                                grantValues.getString("grantedTimestamp"));
                        if (isInserted)
                        {
                            isUpdated=true;
                        }
                    }
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
                    if(databaseHelper.noDuplicate(id))
                    {
                        boolean isInserted =
                                databaseHelper.insertData(
                                registrationValues.getString("cbsdId"),
                                locationInfoValues.getString("countyName"),
                                grantValues.getString("operationState"),
                                installationParamValues.getDouble("latitude"),
                                installationParamValues.getDouble("longitude"),
                                "",
                                "0",
                                "0",
                                0.0,
                                "");
                        if (isInserted)
                        {
                            isUpdated=true;
                        }
                    }
                }
                totalParse=totalParse+singleParse;
            }
            return totalParse;
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
    public void onPostExecute(String response)
    {
        if (response != null)
        {
            super.onPostExecute(response);
            p.dismiss();
            MainActivity.data.setText(this.totalParse);
            if(isUpdated==false) {
                Toast.makeText(context, "No Updated Information", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(context, "There is Updated Information", Toast.LENGTH_LONG).show();
            }
        }
    }
}
