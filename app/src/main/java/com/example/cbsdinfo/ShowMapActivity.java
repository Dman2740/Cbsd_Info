package com.example.cbsdinfo;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class ShowMapActivity extends FragmentActivity implements OnMapReadyCallback
{
    DatabaseHelper db;
    FusedLocationProviderClient fusedLocationProviderClient;
    String information;
    String snippet;
    Spinner spinner;
    ArrayList<Marker> greenMarkers=new ArrayList<>();
    ArrayList<Marker> redMarkers=new ArrayList<>();
    String currentState;
    GoogleMap map;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        db = new DatabaseHelper(this);
        spinner=findViewById(R.id.spinner);
        populateDropDown();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

    }

    public void updateMap()
    {
        currentState=spinner.getSelectedItem().toString();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        if(currentState.equals("Authorized"))
                        {
                            for(Marker marker: redMarkers)
                            {
                                marker.setVisible(false);
                            }
                        }
                        else if(currentState.equals("Unauthorized"))
                        {
                            for(Marker marker: greenMarkers)
                            {
                                marker.setVisible(false);
                            }
                        }
                        else if(currentState.equals("All"))
                        {
                            for(Marker marker: redMarkers)
                            {
                                marker.setVisible(true);
                            }
                            for(Marker marker: greenMarkers)
                            {
                                marker.setVisible(true);
                            }
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {

                    }
                });
    }
    private void populateDropDown()
    {
        db=new DatabaseHelper(this);
        ArrayList<String> listOperationState = new ArrayList<>();
        listOperationState.add("All");
        listOperationState.add("Authorized");
        listOperationState.add("Unauthorized");
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listOperationState);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map=googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setBuildingsEnabled(true);
        Cursor data = db.getRadioData();
        if (data.getCount() > 0)
        {
            while (data.moveToNext())
            {
                ArrayList<LatLng> latLngArrayList = new ArrayList<>();
                ArrayList<String> listData = new ArrayList<>();
                listData.add(data.getString(1));//id
                listData.add(data.getString(2));//site
                listData.add(data.getString(3));//state
                listData.add(data.getString(4));//latitude
                listData.add(data.getString(5));//longitude
                listData.add(data.getString(6));//grantId
                listData.add(data.getString(7));//lowFrequency
                listData.add(data.getString(8));//highFrequency
                listData.add(data.getString(9));//maxEirp
                listData.add(data.getString(10));//grantTimestamp
                String id = listData.get(0);
                String county = listData.get(1);
                String state=listData.get(2);
                Double lat = Double.parseDouble(listData.get(3));
                Double longy = Double.parseDouble(listData.get(4));
                String grantId=listData.get(5);
                String lowFreq=listData.get(6);
                String highFreq=listData.get(7);
                Double maxEirp=Double.parseDouble(listData.get(8));
                String gTimestamp=listData.get(9);
                LatLng location = new LatLng(lat, longy);
                latLngArrayList.add(location);
                information = id;
                snippet="Site: "+ county +" "+ "\n"+
                "State: "+ state+" "+"\n"+
                "GrantID: "+grantId+" "+"\n"+
                "LowFrequency: "+lowFreq+" "+"\n"+
                "HighFrequency: "+highFreq+" "+"\n"+
                "MaxEirp: "+ maxEirp+" "+"\n"+
                "GrantTimeStamp: "+gTimestamp+" "+"\n";
                if(state.equalsIgnoreCase("AUTHORIZED"))
                {
                    Marker gMarker=map.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, longy))
                            .title(information)
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    greenMarkers.add(gMarker);
                }
                else if(state.equalsIgnoreCase("NONE"))
                {
                    Marker rMarker=map.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, longy))
                            .title(information)
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    redMarkers.add(rMarker);
                }
            }
        }
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter(ShowMapActivity.this));
        updateMap();
    }
}
