package com.example.cbsdinfo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    Button click;
    Context contexty;
    String basicAuth = "";
    String userId = "";
    String TAG="MainActivity";
    FusedLocationProviderClient fusedLocationProviderClient;
    DatabaseReference reference;
    GoogleMap mapy;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        reference = FirebaseDatabase.getInstance().getReference();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            basicAuth = extras.getString("key");
            userId = extras.getString("userId");
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        click = findViewById(R.id.btnInfo);
        contexty = MainActivity.this;
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowJsonInfo task = new ShowJsonInfo(contexty, reference, basicAuth);
                task.execute();
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mapy=googleMap;
        reference.child(userId).child("CBSD Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot childSnap:snapshot.getChildren()) {
                    String gId=childSnap.child("Grant ID").getValue().toString();
                    String gTs=childSnap.child("Grant Timestamp").getValue().toString();
                    String hF=childSnap.child("High Frequency").getValue().toString();
                    String id=childSnap.child("ID").getValue().toString();
                    String lat=childSnap.child("Latitude").getValue().toString();
                    String longy=childSnap.child("Longitude").getValue().toString();
                    Double lat1=Double.parseDouble(lat);
                    Double long1=Double.parseDouble(longy);
                    String lF=childSnap.child("Low Frequency").getValue().toString();
                    String mE=childSnap.child("Max Eirp").getValue().toString();
                    Double maxE=Double.parseDouble(mE);
                    String oS=childSnap.child("Operation State").getValue().toString();
                    String site=childSnap.child("Site").getValue().toString();

                    LatLng location=new LatLng(lat1,long1);
                    String snippet="Site: "+ site +" "+ "\n"+
                            "State: "+ oS+" "+"\n"+
                            "GrantID: "+gId+" "+"\n"+
                            "LowFrequency: "+lF+" "+"\n"+
                            "HighFrequency: "+hF+" "+"\n"+
                            "MaxEirp: "+ maxE+" "+"\n"+
                            "GrantTimeStamp: "+gTs+" "+"\n";

                    if(oS.equalsIgnoreCase("AUTHORIZED"))
                    {
                        mapy.addMarker(new MarkerOptions()
                                .position(location)
                                .title(id)
                                .snippet(snippet)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    }
                    else if(oS.equalsIgnoreCase("NONE"))
                    {
                        mapy.addMarker(new MarkerOptions()
                                .position(location)
                                .title(id)
                                .snippet(snippet)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {}
        });
        mapy.setInfoWindowAdapter(new CustomInfoWindowAdapter(MainActivity.this));
    }
}
