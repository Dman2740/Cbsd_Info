package com.example.cbsdinfo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterScreen extends Activity

{
    DatabaseHelper db;
    TextView textPw,textEmail,textNumber,textConfirm,textNamey;
    Button btnRegister,btnLog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_screen);
        textPw=findViewById(R.id.textPassword);
        textEmail=findViewById(R.id.emailAddress);
        textNumber=findViewById(R.id.phoneNum);
        textNamey=findViewById(R.id.editNamey);
        textConfirm=findViewById(R.id.textPassword2);
        btnRegister=findViewById(R.id.buttonRegister);
        btnLog=findViewById(R.id.buttonLogin);
        db=new DatabaseHelper(this);
        createRegisterUser();
        loginUser();
    }

    public void createRegisterUser()
    {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String emailStr=textEmail.getText().toString();
                String userNamey=textNamey.getText().toString();
                String pwStr=textPw.getText().toString();
                String pnStr=textNumber.getText().toString();
                String pwConfirm=textConfirm.getText().toString();
                if(emailStr.equals("")||textPw.equals("")||textNumber.equals("")||textConfirm.equals("")||textNamey.equals(""))
                {
                    Toast.makeText(RegisterScreen.this, "Please enter all of the boxes", Toast.LENGTH_LONG).show();
                }
                else
                {
                    if (pwStr.equals(pwConfirm))
                    {
                        Boolean checkEmail=db.checkEmail(emailStr);
                        if(checkEmail==true)
                        {
                            boolean isInserted=db.insertUser(userNamey,emailStr,pnStr,pwStr);
                            if(isInserted==true)
                            {
                                Toast.makeText(RegisterScreen.this, "New User Registered Successfully", Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(RegisterScreen.this, "Email Already in Use", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(RegisterScreen.this, "Passwords Do Not Match", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

    }
    public void loginUser()
    {
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i=new Intent(RegisterScreen.this,LoginScreen.class);
                startActivity(i);
            }
        });

    }

}
