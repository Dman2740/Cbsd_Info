package com.example.cbsdinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginScreen extends Activity
{
    private static final String TAG = "FirebaseMessagingServce";
    EditText e1,e2;
    Button b1;
    String token;
    Context contexty;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        reference=FirebaseDatabase.getInstance().getReference();
        contexty=LoginScreen.this;
        setContentView(R.layout.login_screen);
        mAuth=FirebaseAuth.getInstance();
        e1=findViewById(R.id.emaily);
        e2=findViewById(R.id.pwordy);
        b1=findViewById(R.id.buttlogin);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();

                        // Log and toast
                        Log.d(TAG, "Token:  "+token);
                    }
                });
        authentication();
    }

    public void authentication()
    {
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    String str=e1.getText().toString();
                    String pass=e2.getText().toString();
                    String auth=str+ ":" +pass;
                    byte[] data1=auth.getBytes("UTF-8");
                    String base64=Base64.encodeToString(data1,Base64.DEFAULT);
                    String basicAuth="Basic "+base64;
                    CheckCredentials task=new CheckCredentials(contexty,reference,basicAuth,str,pass,mAuth,token);
                    task.execute();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

}
