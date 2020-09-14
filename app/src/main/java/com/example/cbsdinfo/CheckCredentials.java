package com.example.cbsdinfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    Boolean checkCredential;
    private Context contexty;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    String userId="";
    String authy="";
    String email="";
    String pw="";
    String token="";

    public CheckCredentials(Context context, DatabaseReference ref, String auth,String email,String pw,FirebaseAuth mAuth,String token)
    {
        this.token=token;
        this.mAuth=mAuth;
        this.email=email;
        this.reference=ref;
        this.authy=auth;
        this.contexty=context;
        this.pw=pw;
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
        if(checkCredential==true)
        {
            mAuth.createUserWithEmailAndPassword(email,pw)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                saveToken(token);
                                ShowJsonInfo tasky = new ShowJsonInfo(contexty, reference, authy,email,userId);
                                tasky.execute();
                            }
                            else
                            {
                                if(task.getException() instanceof FirebaseAuthUserCollisionException)
                                {
                                    userLogin(email,pw);
                                }
                                else
                                {
                                    Toast.makeText(contexty,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        private void saveToken(String token) {
                            String userEmaily=mAuth.getCurrentUser().getEmail();
                            User user=new User(userEmaily,token);
                            reference.child(userId).child("Users").child(mAuth.getCurrentUser().getUid()).setValue(user);
                        }

                        private void userLogin(String emaily, String pw)
                        {
                            mAuth.signInWithEmailAndPassword(emaily,pw)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful())
                                            {
                                                saveToken(token);
                                                ShowJsonInfo tasky = new ShowJsonInfo(contexty, reference, authy,email,userId);
                                                tasky.execute();
                                            }
                                            else
                                            {
                                                Toast.makeText(contexty,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    });
        }
        else
        {
            Toast.makeText(contexty,"Credentials Were Not Valid",Toast.LENGTH_LONG).show();
        }

    }
}
