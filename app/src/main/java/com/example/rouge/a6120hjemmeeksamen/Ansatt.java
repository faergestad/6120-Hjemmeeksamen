package com.example.rouge.a6120hjemmeeksamen;

import org.json.JSONObject;

public class Ansatt {


    private int bNR;
    private String fornavn, etternavn, epost, passord, bilde;


    public Ansatt(int bNR, String fornavn, String etternavn, String epost, String passord, String bilde) {
        this.bNR        = bNR;
        this.fornavn    = fornavn;
        this.etternavn  = etternavn;
        this.epost      = epost;
        this.passord    = passord;
        this.bilde      = bilde;

    }

    public Ansatt(JSONObject jsonAnsatt) {
        this.bNR= jsonAnsatt.optInt("BNr");
        this.fornavn = jsonAnsatt.optString("Fornavn");
        this.etternavn = jsonAnsatt.optString("Etternavn");
        this.epost = jsonAnsatt.optString("Epost");
        this.passord = jsonAnsatt.optString("Passord");
        this.bilde = jsonAnsatt.optString("bilde");

    }

    // gettere
    public int getbNR() {
        return bNR;
    }

    public String getFornavn() {
        return fornavn;
    }

    public String getEtternavn() {
        return etternavn;
    }

    public String getEpost() {
        return epost;
    }

    public String getPassord() {
        return passord;
    }

    public String getBilde() {
        return bilde;
    }
    // settere

    public void setbNR(int bNR) {
        this.bNR = bNR;
    }

    public void setFornavn(String fornavn) {
        this.fornavn = fornavn;
    }

    public void setEtternavn(String etternavn) {
        this.etternavn = etternavn;
    }

    public void setEpost(String epost) {
        this.epost = epost;
    }

    public void setPassord(String passord) {
        this.passord = passord;
    }

    public void setBilde(String bilde) {
        this.bilde = bilde;
    }
}
