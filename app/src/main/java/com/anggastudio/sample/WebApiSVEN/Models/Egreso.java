package com.anggastudio.sample.WebApiSVEN.Models;

public class Egreso {

    private Integer id;
    private String terminalID;
    private Integer turno;
    private String fechaProceso;
    private Integer monedaID;
    private String monedaDs;
    private Integer egresoID;
    private String egresoDs;
    private Double mtoTotal;
    private String observacion;
    private String userID;
    private String identFID;
    private String anulado;

    public Egreso(String terminalID, Integer turno, String fechaProceso, Integer monedaID, Integer egresoID, Double mtoTotal, String observacion, String userID, String identFID) {
        this.terminalID = terminalID;
        this.turno = turno;
        this.fechaProceso = fechaProceso;
        this.monedaID = monedaID;
        this.egresoID = egresoID;
        this.mtoTotal = mtoTotal;
        this.observacion = observacion;
        this.userID = userID;
        this.identFID = identFID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    public Integer getTurno() {
        return turno;
    }

    public void setTurno(Integer turno) {
        this.turno = turno;
    }

    public String getFechaProceso() {
        return fechaProceso;
    }

    public void setFechaProceso(String fechaProceso) {
        this.fechaProceso = fechaProceso;
    }

    public Integer getMonedaID() {
        return monedaID;
    }

    public void setMonedaID(Integer monedaID) {
        this.monedaID = monedaID;
    }

    public Integer getEgresoID() {
        return egresoID;
    }

    public void setEgresoID(Integer egresoID) {
        this.egresoID = egresoID;
    }

    public Double getMtoTotal() {
        return mtoTotal;
    }

    public void setMtoTotal(Double mtoTotal) {
        this.mtoTotal = mtoTotal;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getIdentFID() {
        return identFID;
    }

    public void setIdentFID(String identFID) {
        this.identFID = identFID;
    }

    public String getAnulado() {
        return anulado;
    }

    public void setAnulado(String anulado) {
        this.anulado = anulado;
    }

    public String getMonedaDs() {
        return monedaDs;
    }

    public void setMonedaDs(String monedaDs) {
        this.monedaDs = monedaDs;
    }

    public String getEgresoDs() {
        return egresoDs;
    }

    public void setEgresoDs(String egresoDs) {
        this.egresoDs = egresoDs;
    }
}
