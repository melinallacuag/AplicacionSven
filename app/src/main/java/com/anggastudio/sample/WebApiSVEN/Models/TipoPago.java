package com.anggastudio.sample.WebApiSVEN.Models;

public class TipoPago {

    private Integer cardID;
    private String names;

    public TipoPago( String names) {
        this.names  = names;
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
