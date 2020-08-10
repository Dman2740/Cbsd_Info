package com.example.cbsdinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginScreen extends Activity
{

    EditText e1,e2;
    Button b1,b2;
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        db=new DatabaseHelper(this);
        e1=findViewById(R.id.emaily);
        e2=findViewById(R.id.pwordy);
        b1=findViewById(R.id.buttlogin);
        b2=findViewById(R.id.buttonCreateNewUser);
        authentication();
        createNewUser();

    }

    public void authentication()
    {
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String str=e1.getText().toString();
                String pass=e2.getText().toString();

                String password=db.checkCredentials(str);

                if(pass.equals(password))
                {
                    Toast temp=Toast.makeText(LoginScreen.this,"Login Successfull",Toast.LENGTH_SHORT);
                    temp.show();
                    Intent i=new Intent(LoginScreen.this,MainActivity.class);
                    startActivity(i);
                }
                else
                {
                    Toast temp=Toast.makeText(LoginScreen.this,"Login Unsuccessful, Password and Email Do Not Match",Toast.LENGTH_SHORT);
                    temp.show();
                }

            }
        });
    }

    public void createNewUser()
    {
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i=new Intent(LoginScreen.this,RegisterScreen.class);
                startActivity(i);
            }
        });

    }

}
