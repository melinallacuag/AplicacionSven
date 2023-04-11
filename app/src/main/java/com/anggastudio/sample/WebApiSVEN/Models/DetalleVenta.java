package com.anggastudio.sample.WebApiSVEN.Models;

public class DetalleVenta {

    private String cara;
    private String tipoPago;
    private Double impuesto;
    private String nroPlaca;
    private String tarjetaPuntos;
    private String clienteID;
    private String clienteRUC;
    private String clienteRS;
    private String clienteDR;
    private String tarjetaND;
    private String tarjetaCredito;
    private String operacionREF;
    private String observacion;
    private String kilometraje;
    private Double montoSoles;
    private Double mtoSaldoCredito;
    private Double ptosDisponible;

    public DetalleVenta(String cara, String tipoPago, Double impuesto, String nroPlaca, String tarjetaPuntos, String clienteID, String clienteRUC, String clienteRS, String clienteDR, String tarjetaND, String tarjetaCredito, String operacionREF, String observacion, String kilometraje, Double montoSoles, Double mtoSaldoCredito, Double ptosDisponible) {
        this.cara = cara;
        this.tipoPago = tipoPago;
        this.impuesto = impuesto;
        this.nroPlaca = nroPlaca;
        this.tarjetaPuntos = tarjetaPuntos;
        this.clienteID = clienteID;
        this.clienteRUC = clienteRUC;
        this.clienteRS = clienteRS;
        this.clienteDR = clienteDR;
        this.tarjetaND = tarjetaND;
        this.tarjetaCredito = tarjetaCredito;
        this.operacionREF = operacionREF;
        this.observacion = observacion;
        this.kilometraje = kilometraje;
        this.montoSoles = montoSoles;
        this.mtoSaldoCredito = mtoSaldoCredito;
        this.ptosDisponible = ptosDisponible;
    }

    public String getCara() {
        return cara;
    }

    public void setCara(String cara) {
        this.cara = cara;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public Double getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(Double impuesto) {
        this.impuesto = impuesto;
    }

    public String getNroPlaca() {
        return nroPlaca;
    }

    public void setNroPlaca(String nroPlaca) {
        this.nroPlaca = nroPlaca;
    }

    public String getTarjetaPuntos() {
        return tarjetaPuntos;
    }

    public void setTarjetaPuntos(String tarjetaPuntos) {
        this.tarjetaPuntos = tarjetaPuntos;
    }

    public String getClienteID() {
        return clienteID;
    }

    public void setClienteID(String clienteID) {
        this.clienteID = clienteID;
    }

    public String getClienteRUC() {
        return clienteRUC;
    }

    public void setClienteRUC(String clienteRUC) {
        this.clienteRUC = clienteRUC;
    }

    public String getClienteRS() {
        return clienteRS;
    }

    public void setClienteRS(String clienteRS) {
        this.clienteRS = clienteRS;
    }

    public String getClienteDR() {
        return clienteDR;
    }

    public void setClienteDR(String clienteDR) {
        this.clienteDR = clienteDR;
    }

    public String getTarjetaND() {
        return tarjetaND;
    }

    public void setTarjetaND(String tarjetaND) {
        this.tarjetaND = tarjetaND;
    }

    public String getTarjetaCredito() {
        return tarjetaCredito;
    }

    public void setTarjetaCredito(String tarjetaCredito) {
        this.tarjetaCredito = tarjetaCredito;
    }

    public String getOperacionREF() {
        return operacionREF;
    }

    public void setOperacionREF(String operacionREF) {
        this.operacionREF = operacionREF;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(String kilometraje) {
        this.kilometraje = kilometraje;
    }

    public Double getMontoSoles() {
        return montoSoles;
    }

    public void setMontoSoles(Double montoSoles) {
        this.montoSoles = montoSoles;
    }

    public Double getMtoSaldoCredito() {
        return mtoSaldoCredito;
    }

    public void setMtoSaldoCredito(Double mtoSaldoCredito) {
        this.mtoSaldoCredito = mtoSaldoCredito;
    }

    public Double getPtosDisponible() {
        return ptosDisponible;
    }

    public void setPtosDisponible(Double ptosDisponible) {
        this.ptosDisponible = ptosDisponible;
    }
}
