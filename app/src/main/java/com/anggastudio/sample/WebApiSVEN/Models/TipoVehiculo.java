package com.anggastudio.sample.WebApiSVEN.Models;

public class TipoVehiculo {

    private Integer cardID;
    private String names;

    public TipoVehiculo(Integer cardID, String names) {
        this.cardID = cardID;
        this.names = names;
    }

    public Integer getCardID() {
        return cardID;
    }

    public void setCardID(Integer cardID) {
        this.cardID = cardID;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }
}
