package com.anggastudio.sample.WebApiSVEN.Models;

public class VProducto {

    private String articuloID;
    private String articuloDS;
    private Double cantidad;
    private Double soles;
    private Double descuento;
    private Double incremento;

    public VProducto(String articuloDS, Double cantidad, Double soles, Double descuento,Double incremento) {
        this.articuloDS = articuloDS;
        this.cantidad = cantidad;
        this.soles = soles;
        this.descuento = descuento;
        this.incremento = incremento;
    }

    public String getArticuloID() {
        return articuloID;
    }

    public void setArticuloID(String articuloID) {
        this.articuloID = articuloID;
    }

    public String getArticuloDS() {
        return articuloDS;
    }

    public void setArticuloDS(String articuloDS) {
        this.articuloDS = articuloDS;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public Double getSoles() {
        return soles;
    }

    public void setSoles(Double soles) {
        this.soles = soles;
    }

    public Double getDescuento() {
        return descuento;
    }

    public void setDescuento(Double descuento) {
        this.descuento = descuento;
    }

    public Double getIncremento() {
        return incremento;
    }

    public void setIncremento(Double incremento) {
        this.incremento = incremento;
    }
}
