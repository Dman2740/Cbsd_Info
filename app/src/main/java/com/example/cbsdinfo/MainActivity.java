package com.example.cbsdinfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    Button click,btnOpenMap;
    public static TextView data;
    Context contexty;
    DatabaseHelper databaseHelper;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.content_main);
        databaseHelper=new DatabaseHelper(this);
        click=findViewById(R.id.btnInfo);
        data=findViewById(R.id.textView);
        contexty=MainActivity.this;
        btnOpenMap = findViewById(R.id.buttonOpenMap);
        gotoMap();
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ShowJsonInfo task=new ShowJsonInfo(contexty,databaseHelper);
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
