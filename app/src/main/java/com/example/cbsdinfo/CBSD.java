package com.example.cbsdinfo;

public class CBSD
{
    public String grantID;
    public String grantTimestamp;
    public String highFreq;
    public String id;
    public Double lat;
    public Double longitude;
    public String lowFreq;
    public Double maxEirp;
    public String opState;
    public String site;

    public CBSD()
    {

    }
    public CBSD(String grantID, String grantTimestamp, String highFreq,
                String id, Double lat, Double longitude, String lowFreq,
                Double maxEirp, String opState, String site)
    {
        this.grantID=grantID;
        this.grantTimestamp=grantTimestamp;
        this.highFreq=highFreq;
        this.id=id;
        this.lat=lat;
        this.longitude=longitude;
        this.lowFreq=lowFreq;
        this.maxEirp=maxEirp;
        this.opState=opState;
        this.site=site;
    }

    public String getGrantID() {
        return grantID;
    }

    public void setGrantID(String grantID) {
        this.grantID = grantID;
    }

    public String getGrantTimestamp() {
        return grantTimestamp;
    }

    public void setGrantTimestamp(String grantTimestamp) {
        this.grantTimestamp = grantTimestamp;
    }

    public String getHighFreq() {
        return highFreq;
    }

    public void setHighFreq(String highFreq) {
        this.highFreq = highFreq;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {this.longitude = longitude;}

    public String getLowFreq() {
        return lowFreq;
    }

    public void setLowFreq(String lowFreq) {
        this.lowFreq = lowFreq;
    }

    public Double getMaxEirp() {
        return maxEirp;
    }

    public void setMaxEirp(Double maxEirp) {
        this.maxEirp = maxEirp;
    }

    public String getOpState() {
        return opState;
    }

    public void setOpState(String opState) {
        this.opState = opState;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }



}
