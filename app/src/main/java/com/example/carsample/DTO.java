package com.example.carsample;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class DTO implements Serializable{
    private String addr;
    private String chargeTp;
    private String cpld;
    private String cpNm;
    private String cpStat;
    private String cpTp;
    private String csld;
    private String csNm;
    private String lat;
    private String longi;
    private String statUpdateDatetime;

    public DTO() { }

    public DTO(String addr, String chargeTp, String cpld, String cpNm, String cpStat, String cpTp, String csld, String csNm, String lat, String longi, String statUpdateDatetime) {
        this.addr = addr;
        this.chargeTp = chargeTp;
        this.cpld = cpld;
        this.cpNm = cpNm;
        this.cpStat = cpStat;
        this.cpTp = cpTp;
        this.csld = csld;
        this.csNm = csNm;
        this.lat = lat;
        this.longi = longi;
        this.statUpdateDatetime = statUpdateDatetime;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getChargeTp() {
        return chargeTp;
    }

    public void setChargeTp(String chargeTp) {
        this.chargeTp = chargeTp;
    }

    public String getCpld() {
        return cpld;
    }

    public void setCpld(String cpld) {
        this.cpld = cpld;
    }

    public String getCpNm() {
        return cpNm;
    }

    public void setCpNm(String cpNm) {
        this.cpNm = cpNm;
    }

    public String getCpStat() {
        return cpStat;
    }

    public void setCpStat(String cpStat) {
        this.cpStat = cpStat;
    }

    public String getCpTp() {
        return cpTp;
    }

    public void setCpTp(String cpTp) {
        this.cpTp = cpTp;
    }

    public String getCsld() {
        return csld;
    }

    public void setCsld(String csld) {
        this.csld = csld;
    }

    public String getCsNm() {
        return csNm;
    }

    public void setCsNm(String csNm) {
        this.csNm = csNm;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getStatUpdateDatetime() {
        return statUpdateDatetime;
    }

    public void setStatUpdateDatetime(String statUpdateDatetime) {
        this.statUpdateDatetime = statUpdateDatetime;
    }

}
