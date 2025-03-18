package com.anggastudio.sample.WebApiSVEN.Models;

public class CambioPrecios {

    private Double regular;
    private Double premium;
    private Double glp;
    private Double diesel;
    private Double urea;

    public CambioPrecios(Double regular, Double premium, Double glp, Double diesel, Double urea) {
        this.regular = regular;
        this.premium = premium;
        this.glp = glp;
        this.diesel = diesel;
        this.urea = urea;
    }

    public Double getRegular() {
        return regular;
    }

    public void setRegular(Double regular) {
        this.regular = regular;
    }

    public Double getPremium() {
        return premium;
    }

    public void setPremium(Double premium) {
        this.premium = premium;
    }

    public Double getGlp() {
        return glp;
    }

    public void setGlp(Double glp) {
        this.glp = glp;
    }

    public Double getDiesel() {
        return diesel;
    }

    public void setDiesel(Double diesel) {
        this.diesel = diesel;
    }

    public Double getUrea() {
        return urea;
    }

    public void setUrea(Double urea) {
        this.urea = urea;
    }
}
