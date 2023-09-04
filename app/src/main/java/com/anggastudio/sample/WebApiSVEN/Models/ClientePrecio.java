package com.anggastudio.sample.WebApiSVEN.Models;

public class ClientePrecio {

    private  String rfid;
    private  String articuloID;
    private  String clienteID;
    private  String tipoCliente;
    private  String tipoRango;
    private  Double rango1;
    private  Double rango2;
    private  String clienteRZ;
    private  String nroPlaca;
    private  String tipoDescuento;
    private  Double montoDescuento;
    private  String companyID;
    private  String userID;

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getArticuloID() {
        return articuloID;
    }

    public void setArticuloID(String articuloID) {
        this.articuloID = articuloID;
    }

    public String getClienteID() {
        return clienteID;
    }

    public void setClienteID(String clienteID) {
        this.clienteID = clienteID;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
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

    public String getClienteRZ() {
        return clienteRZ;
    }

    public void setClienteRZ(String clienteRZ) {
        this.clienteRZ = clienteRZ;
    }

    public String getNroPlaca() {
        return nroPlaca;
    }

    public void setNroPlaca(String nroPlaca) {
        this.nroPlaca = nroPlaca;
    }

    public String getTipoDescuento() {
        return tipoDescuento;
    }

    public void setTipoDescuento(String tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
    }

    public Double getMontoDescuento() {
        return montoDescuento;
    }

    public void setMontoDescuento(Double montoDescuento) {
        this.montoDescuento = montoDescuento;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
