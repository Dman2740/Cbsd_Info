package com.example.cbsdinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.collections.MarkerManager;

import java.io.FileInputStream;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String CHANNEL_ID="CBSD_Notification";
    Context contexty;
    String userId = "";
    FusedLocationProviderClient fusedLocationProviderClient;
    DatabaseReference reference;
    ClusterManager<MyItem> clusterManager;


    GoogleMap mapy;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        reference = FirebaseDatabase.getInstance().getReference();
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            userId = extras.getString("userId");
        }
        contexty = MainActivity.this;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
    }

    private void setUpClusterer(GoogleMap map) {
        clusterManager = new ClusterManager<MyItem>(this, map);
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        map.setOnInfoWindowClickListener(clusterManager);
        clusterManager.setAnimation(true);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap)
    {
        mapy=googleMap;
        setUpClusterer(mapy);
        reference.child(userId).child("CBSD Info").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot childSnap:snapshot.getChildren())
                {
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

                    String snippet="Site: "+ site +" "+ "\n"+
                            "State: "+ oS+" "+"\n"+
                            "GrantID: "+gId+" "+"\n"+
                            "LowFrequency: "+lF+" "+"\n"+
                            "HighFrequency: "+hF+" "+"\n"+
                            "MaxEirp: "+ maxE+" "+"\n"+
                            "GrantTimeStamp: "+gTs+" "+"\n";

                    double offset=1/60d;
                    lat1+=offset;
                    long1+=offset;
                    MyItem offsetItem=new MyItem(lat1,long1,id,snippet);
                    clusterManager.addItem(offsetItem);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {}
        });
    }

}
