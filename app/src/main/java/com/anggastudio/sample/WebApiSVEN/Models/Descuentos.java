package com.anggastudio.sample.WebApiSVEN.Models;

public class Descuentos {

    private  String clienteID;
    private  String tipoID;
    private  String articuloID;
    private  Double descuento;
    private  String tipoDesc;
    private  String tipoRango;
    private  Double rango1;
    private  Double rango2;

    public String getClienteID() {
        return clienteID;
    }

    public void setClienteID(String clienteID) {
        this.clienteID = clienteID;
    }

    public String getTipoID() {
        return tipoID;
    }

    public void setTipoID(String tipoID) {
        this.tipoID = tipoID;
    }

    public String getArticuloID() {
        return articuloID;
    }

    public void setArticuloID(String articuloID) {
        this.articuloID = articuloID;
    }

    public Double getDescuento() {
        return descuento;
    }

    public void setDescuento(Double descuento) {
        this.descuento = descuento;
    }

    public String getTipoDesc() {
        return tipoDesc;
    }

    public void setTipoDesc(String tipoDesc) {
        this.tipoDesc = tipoDesc;
    }

    public String getTipoRango() {
        return tipoRango;
    }

    public void setTipoRango(String tipoRango) {
        this.tipoRango = tipoRango;
    }

    public Double getRango1() {
        return rango1;
    }

    public void setRango1(Double rango1) {
        this.rango1 = rango1;
    }

    public Double getRango2() {
        return rango2;
    }

    public void setRango2(Double rango2) {
        this.rango2 = rango2;
    }

}
