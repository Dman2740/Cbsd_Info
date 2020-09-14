package com.example.cbsdinfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem
{
    public LatLng mPosition;
    public String mTitle;
    public String mSnippet;

    public MyItem(double lat,double lang)
    {
        mPosition=new LatLng(lat,lang);
    }

    public MyItem(double lat,double lng,String title,String snippet)
    {
        mPosition=new LatLng(lat,lng);
        mTitle=title;
        mSnippet=snippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }


    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}
