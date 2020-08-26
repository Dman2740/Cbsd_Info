package com.example.cbsdinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity
{
    Button click,btnOpenMap;
    public static TextView data;
    Context contexty;
    DatabaseHelper databaseHelper;
    FusedLocationProviderClient fusedLocationProviderClient;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //FirebaseDatabase database=FirebaseDatabase.getInstance().getReference();
        //reference=database.getReference("CBSD Info");
        reference=FirebaseDatabase.getInstance().getReference();
        reference.child("CBSD INFO");
        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.content_main);
        databaseHelper=new DatabaseHelper(this);
        click=findViewById(R.id.btnInfo);
        data=findViewById(R.id.textView);
        contexty=MainActivity.this;
        btnOpenMap = findViewById(R.id.buttonOpenMap);
        gotoMap();
        click.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ShowJsonInfo task=new ShowJsonInfo(contexty,databaseHelper,reference);
                task.execute();
            }
        });
    }

    private void gotoMap() {
        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this,ShowMapActivity.class));
            }
        });
    }
}
