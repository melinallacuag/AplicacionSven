package com.anggastudio.sample.WebApiSVEN.Models;

public class LClientePuntos {

    private String rfid;
    private String articuloID;
    private String clienteID;
    private String clienteRZ;
    private String nroPlaca;
    private Double ganados;
    private Double canjeados;
    private Double disponibles;
    private Boolean status;
    private String companyID;
    private String userID;

    public LClientePuntos(String rfid, String articuloID, String clienteID, String clienteRZ, String nroPlaca, Double ganados, Double canjeados, Double disponibles, Boolean status, String companyID, String userID) {
        this.rfid = rfid;
        this.articuloID = articuloID;
        this.clienteID = clienteID;
        this.clienteRZ = clienteRZ;
        this.nroPlaca = nroPlaca;
        this.ganados = ganados;
        this.canjeados = canjeados;
        this.disponibles = disponibles;
        this.status = status;
        this.companyID = companyID;
        this.userID = userID;
    }

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

    public Double getGanados() {
        return ganados;
    }

    public void setGanados(Double ganados) {
        this.ganados = ganados;
    }

    public Double getCanjeados() {
        return canjeados;
    }

    public void setCanjeados(Double canjeados) {
        this.canjeados = canjeados;
    }

    public Double getDisponibles() {
        return disponibles;
    }

    public void setDisponibles(Double disponibles) {
        this.disponibles = disponibles;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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
