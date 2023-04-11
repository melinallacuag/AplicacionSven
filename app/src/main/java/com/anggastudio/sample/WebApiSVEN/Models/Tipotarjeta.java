package com.anggastudio.sample.WebApiSVEN.Models;

public class Tipotarjeta {

    private String idTarjeta;
    private String nombreTarjeta;

    public Tipotarjeta(String idTarjeta,String nombreTarjeta) {
        this.idTarjeta = idTarjeta;
        this.nombreTarjeta = nombreTarjeta;
    }

    public String getIdTarjeta() {
        return idTarjeta;
    }

    public void setIdTarjeta(String idTarjeta) {
        this.idTarjeta = idTarjeta;
    }

    public String getNombreTarjeta() {
        return nombreTarjeta;
    }

    public void setNombreTarjeta(String nombreTarjeta) {
        this.nombreTarjeta = nombreTarjeta;
    }
}
