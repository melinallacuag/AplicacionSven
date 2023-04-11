package com.anggastudio.sample.WebApiSVEN.Models;

public class Cliente {

    private String clienteID;
    private String clienteRUC;
    private String clienteRZ;
    private String clienteDR;
    private Boolean consulta_Sunat;
    private Double dias_Credito;
    private String tipo_Cliente;

    public Cliente(String clienteID, String clienteRUC) {
        this.clienteID = clienteID;
        this.clienteRUC = clienteRUC;
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

    public Boolean getConsulta_Sunat() {
        return consulta_Sunat;
    }

    public void setConsulta_Sunat(Boolean consulta_Sunat) {
        this.consulta_Sunat = consulta_Sunat;
    }

    public Double getDias_Credito() {
        return dias_Credito;
    }

    public void setDias_Credito(Double dias_Credito) {
        this.dias_Credito = dias_Credito;
    }

    public String getTipo_Cliente() {
        return tipo_Cliente;
    }

    public void setTipo_Cliente(String tipo_Cliente) {
        this.tipo_Cliente = tipo_Cliente;
    }
}
