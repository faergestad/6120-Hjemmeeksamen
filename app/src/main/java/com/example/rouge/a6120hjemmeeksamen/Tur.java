package com.example.rouge.a6120hjemmeeksamen;

import android.support.v7.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

public class Tur {


    private int turID, aarsModell, ledigeSeter;
    private String sjafor, avreiseSted, reiseMaal, dato, avreiseKl, reiseTidMinutter,
            passasjer1, passasjer2, passasjer3, passasjer4, bilmerke;
    
    private static final String KOL_NAVN_TUR_ID             = "turID";
    private static final String KOL_NAVN_SJAFOR             = "sjafor";
    private static final String KOL_NAVN_AVREISESTED        = "avreiseSted";
    private static final String KOL_NAVN_REISEMAAL          = "reiseMaal";
    private static final String KOL_NAVN_DATO               = "dato";
    private static final String KOL_NAVN_AVREISEKL          = "avreiseKl";
    private static final String KOL_NAVN_REISETIDMINUTTER   = "reiseTidMinutter";
    private static final String KOL_NAVN_PASSASJER1         = "passasjer1";
    private static final String KOL_NAVN_PASSASJER2         = "passasjer2";
    private static final String KOL_NAVN_PASSASJER3         = "passasjer3";
    private static final String KOL_NAVN_PASSASJER4         = "passasjer4";
    private static final String KOL_NAVN_BILMERKE           = "bilmerke";
    private static final String KOL_NAVN_AARSMODELL         = "aarsModell";
    private static final String KOL_NAVN_LEDIGESETER        = "ledigeSeter";

    public Tur(){

    }

    public Tur(int turID, String sjafor, String avreiseSted, String reiseMaal, String dato, String avreiseKl,
               String reiseTidMinutter, String passasjer1, String passasjer2, String passasjer3, String passasjer4, int ledigeSeter, String bilmerke, int aarsModell) {

        this.turID              = turID;
        this.sjafor             = sjafor;
        this.avreiseSted        = avreiseSted;
        this.reiseMaal          = reiseMaal;
        this.dato               = dato;
        this.avreiseKl          = avreiseKl;
        this.reiseTidMinutter   = reiseTidMinutter;
        this.passasjer1         = passasjer1;
        this.passasjer2         = passasjer2;
        this.passasjer3         = passasjer3;
        this.passasjer4         = passasjer4;
        this.ledigeSeter        = ledigeSeter;
        this.bilmerke           = bilmerke;
        this.aarsModell         = aarsModell;

    }

    public Tur(JSONObject jsonTur) {
        this.turID              = jsonTur.optInt("turID");
        this.sjafor             = jsonTur.optString("sjafor");
        this.avreiseSted        = jsonTur.optString("avreiseSted");
        this.reiseMaal          = jsonTur.optString("reiseMaal");
        this.dato               = jsonTur.optString("dato");
        this.avreiseKl          = jsonTur.optString("avreiseKl");
        this.reiseTidMinutter   = jsonTur.optString("reiseTidMinutter");
        this.passasjer1         = jsonTur.optString("passasjer1");
        this.passasjer2         = jsonTur.optString("passasjer2");
        this.passasjer3         = jsonTur.optString("passasjer3");
        this.passasjer4         = jsonTur.optString("passasjer4");
        this.ledigeSeter        = jsonTur.optInt("ledigeSeter");
        this.bilmerke           = jsonTur.optString("bilmerke");
        this.aarsModell         = jsonTur.optInt("aarsModell");
    }

    public JSONObject lagJSONObject () {
        JSONObject jsonTur = new JSONObject();

        try {
            jsonTur.put(KOL_NAVN_TUR_ID, this.turID);
            jsonTur.put(KOL_NAVN_SJAFOR, this.sjafor);
            jsonTur.put(KOL_NAVN_AVREISESTED, this.avreiseSted);
            jsonTur.put(KOL_NAVN_REISEMAAL, this.reiseMaal);
            jsonTur.put(KOL_NAVN_DATO, this.dato);
            jsonTur.put(KOL_NAVN_AVREISEKL, this.avreiseKl);
            jsonTur.put(KOL_NAVN_REISETIDMINUTTER, this.reiseTidMinutter);
            jsonTur.put(KOL_NAVN_PASSASJER1, this.passasjer1);
            jsonTur.put(KOL_NAVN_PASSASJER2, this.passasjer2);
            jsonTur.put(KOL_NAVN_PASSASJER3, this.passasjer3);
            jsonTur.put(KOL_NAVN_PASSASJER4, this.passasjer4);
            jsonTur.put(KOL_NAVN_LEDIGESETER, this.ledigeSeter);
            jsonTur.put(KOL_NAVN_BILMERKE, this.bilmerke);
            jsonTur.put(KOL_NAVN_AARSMODELL, this.aarsModell);
        } catch (JSONException e) {
            return null;
        }
        return jsonTur;

    }


    // Gettere & Settere
    public int getTurID() {
        return turID;
    }

    public void setTurID(int turID) {
        this.turID = turID;
    }

    public int getAarsModell() {
        return aarsModell;
    }

    public void setAarsModell(int aarsModell) {
        this.aarsModell = aarsModell;
    }

    public String getSjafor() {
        return sjafor;
    }

    public void setSjafor(String sjafor) {
        this.sjafor = sjafor;
    }

    public String getAvreiseSted() {
        return avreiseSted;
    }

    public void setAvreiseSted(String avreiseSted) {
        this.avreiseSted = avreiseSted;
    }

    public String getReiseMaal() {
        return reiseMaal;
    }

    public void setReiseMaal(String reiseMaal) {
        this.reiseMaal = reiseMaal;
    }

    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;
    }

    public String getAvreiseKl() {
        return avreiseKl;
    }

    public void setAvreiseKl(String avreiseKl) {
        this.avreiseKl = avreiseKl;
    }

    public String getReiseTidMinutter() {
        return reiseTidMinutter;
    }

    public void setReiseTidMinutter(String reiseTidMinutter) {
        this.reiseTidMinutter = reiseTidMinutter;
    }

    public String getPassasjer1() {
        return passasjer1;
    }

    public void setPassasjer1(String passasjer1) {
        this.passasjer1 = passasjer1;
    }

    public String getPassasjer2() {
        return passasjer2;
    }

    public void setPassasjer2(String passasjer2) {
        this.passasjer2 = passasjer2;
    }

    public String getPassasjer3() {
        return passasjer3;
    }

    public void setPassasjer3(String passasjer3) {
        this.passasjer3 = passasjer3;
    }

    public String getPassasjer4() {
        return passasjer4;
    }

    public void setPassasjer4(String passasjer4) {
        this.passasjer4 = passasjer4;
    }

    public String getBilmerke() {
        return bilmerke;
    }

    public void setBilmerke(String bilmerke) {
        this.bilmerke = bilmerke;
    }

    public int getLedigeSeter() {
        return ledigeSeter;
    }

    public void setLedigeSeter(int ledigeSeter) {
        this.ledigeSeter = ledigeSeter;
    }
}
