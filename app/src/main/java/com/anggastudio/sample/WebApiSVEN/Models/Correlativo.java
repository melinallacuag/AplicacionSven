package com.anggastudio.sample.WebApiSVEN.Models;

public class Correlativo {

    private String  terminalID;
    private String  imei;
    private String  fechaProceso;
    private Integer turno;
    private String  serie;
    private String  numero;
    private Double  montoDescuento;
    private String  documentoVenta;
    private String  tipoDescuento;
    private Double puntosGanados;
    private Double puntosDisponibles;

    public String getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getFechaProceso() {
        return fechaProceso;
    }

    public void setFechaProceso(String fechaProceso) {
        this.fechaProceso = fechaProceso;
    }

    public Integer getTurno() {
        return turno;
    }

    public void setTurno(Integer turno) {
        this.turno = turno;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Double getMontoDescuento() { return montoDescuento;  }

    public void setMontoDescuento(Double montoDescuento) { this.montoDescuento = montoDescuento; }

    public String getDocumentoVenta() {
        return documentoVenta;
    }

    public void setDocumentoVenta(String documentoVenta) {
        this.documentoVenta = documentoVenta;
    }

    public String getTipoDescuento() {
        return tipoDescuento;
    }

    public void setTipoDescuento(String tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
    }

    public Double getPuntosGanados() {
        return puntosGanados;
    }

    public void setPuntosGanados(Double puntosGanados) {
        this.puntosGanados = puntosGanados;
    }

    public Double getPuntosDisponibles() {
        return puntosDisponibles;
    }

    public void setPuntosDisponibles(Double puntosDisponibles) {
        this.puntosDisponibles = puntosDisponibles;
    }
}
