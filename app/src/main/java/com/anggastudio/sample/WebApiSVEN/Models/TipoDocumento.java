package com.anggastudio.sample.WebApiSVEN.Models;

public class TipoDocumento {

    private String cardID;
    private String names;

    public TipoDocumento(String cardID, String names) {
        this.cardID = cardID;
        this.names = names;
    }

    public String getCardID() {
        return cardID;
    }

    public void setCardID(String cardID) {
        this.cardID = cardID;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

}
