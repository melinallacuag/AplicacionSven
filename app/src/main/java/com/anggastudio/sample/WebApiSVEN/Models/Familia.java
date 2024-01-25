package com.anggastudio.sample.WebApiSVEN.Models;

public class Familia {

    private String familiaID;
    private String familiaDS;

    public Familia(String familiaDS) {
        this.familiaDS = familiaDS;
    }

    public String getFamiliaID() {
        return familiaID;
    }

    public void setFamiliaID(String familiaID) {
        this.familiaID = familiaID;
    }

    public String getFamiliaDS() {
        return familiaDS;
    }

    public void setFamiliaDS(String familiaDS) {
        this.familiaDS = familiaDS;
    }
}
