package com.anggastudio.sample.WebApiSVEN.Models;

public class ReporteVendedor {

    private String nombres;
    private Integer despachos;
    private Double soles;

    public ReporteVendedor(String nombres, Integer despachos, Double soles) {
        this.nombres = nombres;
        this.despachos = despachos;
        this.soles = soles;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public Integer getDespachos() {
        return despachos;
    }

    public void setDespachos(Integer despachos) {
        this.despachos = despachos;
    }

    public Double getSoles() {
        return soles;
    }

    public void setSoles(Double soles) {
        this.soles = soles;
    }
}
