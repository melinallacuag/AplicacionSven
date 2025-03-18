package com.anggastudio.sample.WebApiSVEN.Models;

public class ReporteTVehiculo {

    private String tipovehiculo;
    private Double volumen;
    private Double soles;

    public ReporteTVehiculo(String tipovehiculo, Double volumen, Double soles) {
        this.tipovehiculo = tipovehiculo;
        this.volumen = volumen;
        this.soles = soles;
    }

    public String getTipovehiculo() {
        return tipovehiculo;
    }

    public void setTipovehiculo(String tipovehiculo) {
        this.tipovehiculo = tipovehiculo;
    }

    public Double getVolumen() {
        return volumen;
    }

    public void setVolumen(Double volumen) {
        this.volumen = volumen;
    }

    public Double getSoles() {
        return soles;
    }

    public void setSoles(Double soles) {
        this.soles = soles;
    }
}
