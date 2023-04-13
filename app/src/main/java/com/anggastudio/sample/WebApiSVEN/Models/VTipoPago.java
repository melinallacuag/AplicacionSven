package com.anggastudio.sample.WebApiSVEN.Models;

public class VTipoPago {
    private String pagoID;
    private String names;
    private Double soles;

    public VTipoPago(String names, Double soles) {
        this.names = names;
        this.soles = soles;
    }

    public String getPagoID() {
        return pagoID;
    }

    public void setPagoID(String pagoID) {
        this.pagoID = pagoID;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public Double getSoles() {
        return soles;
    }

    public void setSoles(Double soles) {
        this.soles = soles;
    }
}
