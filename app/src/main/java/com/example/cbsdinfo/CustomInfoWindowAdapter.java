package com.example.cbsdinfo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;


public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
{

    private final View mWindow;
    private Context mContext;
    ProgressBar progressBar;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void rendowWindowText(Marker marker, View view){

        progressBar=view.findViewById(R.id.progressBar);
        Drawable drawable=new ProgressDrawable(0xdd00ff00, 0x4400ff00);
        progressBar.setProgressDrawable(drawable);

        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.title);
        tvTitle.setText(title);

        String snippet=marker.getSnippet();
        TextView tvSnippet=view.findViewById(R.id.snippet);
        tvSnippet.setText(snippet);

        TextView numChan=view.findViewById(R.id.numChannels);
        TextView lowFreq=view.findViewById(R.id.lowFreq);
        TextView highFreq=view.findViewById(R.id.highFreq);

        String lfNum="";
        String hfNum="";

        String outerWords[]=snippet.split("\n");


        for(int i=0;i<outerWords.length;i++)
        {
            String innerWords[]=outerWords[i].split(": ");

            for(int j=0;j<innerWords.length;j++)
            {
                if(innerWords[j].equals("LowFrequency"))
                {
                    lfNum=innerWords[j+1];
                }
                else if(innerWords[j].equals("HighFrequency"))
                {
                    hfNum=innerWords[j+1];
                }
            }
        }

        String actualNum[]=lfNum.split("\\.");

        String fDig=actualNum[0];
        String rest=actualNum[1];

        String lowHz=rest.replace("E9","0 MHz");

        String finalLow=fDig+lowHz;

        String actualNum2[]=hfNum.split("\\.");

        String fDig2=actualNum2[0];
        String rest2=actualNum2[1];

        String highHz=rest2.replace("E9","0 MHz");
        String finalHigh=fDig2+highHz;

        lowFreq.setText(finalLow);
        highFreq.setText(finalHigh);

        String startFreq=finalLow.substring(0,3);
        String endFrequency=finalHigh.substring(0,3);


        Integer lowFrequency=Integer.parseInt(startFreq);
        Integer highFrequency=Integer.parseInt(endFrequency);

        Integer numBars=highFrequency-lowFrequency;
        numBars=numBars*10;
        numChan.setText("The Number of Channels: "+numBars.toString());
    }

    @Override
    public View getInfoWindow(Marker marker) {
        rendowWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        rendowWindowText(marker, mWindow);
        return mWindow;
    }

}
