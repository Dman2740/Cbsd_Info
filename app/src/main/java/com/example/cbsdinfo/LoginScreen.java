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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginScreen extends Activity
{

    EditText e1,e2;
    Button b1;
    Context contexty;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        reference=FirebaseDatabase.getInstance().getReference();
        contexty=LoginScreen.this;
        setContentView(R.layout.login_screen);
        e1=findViewById(R.id.emaily);
        e2=findViewById(R.id.pwordy);
        b1=findViewById(R.id.buttlogin);
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
                    CheckCredentials task=new CheckCredentials(contexty,reference,basicAuth);
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
