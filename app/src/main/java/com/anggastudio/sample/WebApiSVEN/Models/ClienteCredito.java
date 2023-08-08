package com.anggastudio.sample.WebApiSVEN.Models;

public class ClienteCredito {

    private String clienteID;
    private String tarjetaID;
    private String clienteRZ;
    private String clienteDR;
    private String articuloID;
    private String nroPLaca;
    private String tipo;
    private Double limiteMonto;
    private Double consumoMonto;
    private Double saldo;
    private Boolean ilimitado;
    private Boolean bloqueado;
    private Integer companyID;


    public ClienteCredito(String tarjetaID, String clienteRZ, String tipo, Double saldo) {
        this.tarjetaID = tarjetaID;
        this.clienteRZ = clienteRZ;
        this.tipo = tipo;
        this.saldo = saldo;
    }

    public String getClienteID() {
        return clienteID;
    }

    public void setClienteID(String clienteID) {
        this.clienteID = clienteID;
    }

    public String getTarjetaID() {
        return tarjetaID;
    }

    public void setTarjetaID(String tarjetaID) {
        this.tarjetaID = tarjetaID;
    }

    public String getClienteRZ() {
        return clienteRZ;
    }

    public void setClienteRZ(String clienteRZ) {
        this.clienteRZ = clienteRZ;
    }

    public String getClienteDR() {
        return clienteDR;
    }

    public void setClienteDR(String clienteDR) {
        this.clienteDR = clienteDR;
    }

    public String getArticuloID() {
        return articuloID;
    }

    public void setArticuloID(String articuloID) {
        this.articuloID = articuloID;
    }

    public String getNroPLaca() {
        return nroPLaca;
    }

    public void setNroPLaca(String nroPLaca) {
        this.nroPLaca = nroPLaca;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getLimiteMonto() {
        return limiteMonto;
    }

    public void setLimiteMonto(Double limiteMonto) {
        this.limiteMonto = limiteMonto;
    }

    public Double getConsumoMonto() {
        return consumoMonto;
    }

    public void setConsumoMonto(Double consumoMonto) {
        this.consumoMonto = consumoMonto;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public Boolean getIlimitado() {
        return ilimitado;
    }

    public void setIlimitado(Boolean ilimitado) {
        this.ilimitado = ilimitado;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }
}
