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
    private String articuloID;
    private String tipoCliente;
    private String tipoDescuento;
    private Double montoDescuento;

    public DetalleVenta(String cara, String tipoPago, Double impuesto, String nroPlaca, String tarjetaPuntos) {
        this.cara = cara;
        this.tipoPago = tipoPago;
        this.impuesto = impuesto;
        this.nroPlaca = nroPlaca;
        this.tarjetaPuntos = tarjetaPuntos;
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

    public String getArticuloID() {
        return articuloID;
    }

    public void setArticuloID(String articuloID) {
        this.articuloID = articuloID;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
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
}
