package com.anggastudio.sample.WebApiSVEN.Models;

public class ReporteEgreso {

    private Integer id;
    private String egresoDs;
    private Double mtoTotal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEgresoDs() {
        return egresoDs;
    }

    public void setEgresoDs(String egresoDs) {
        this.egresoDs = egresoDs;
    }

    public Double getMtoTotal() {
        return mtoTotal;
    }

    public void setMtoTotal(Double mtoTotal) {
        this.mtoTotal = mtoTotal;
    }
}
