package com.anggastudio.sample.WebApiSVEN.Models;

public class ListaComprobante {

    private String fecha;
    private String tipoDocumento;
    private String serieDocumento;
    private String nroDocumento;
    private String clienteID;
    private String clienteRZ;
    private Double mtoTotal;
    private String anulado;

    public ListaComprobante(String fecha, String clienteID, String clienteRZ, Double mtoTotal, String anulado) {
        this.fecha = fecha;
        this.clienteID = clienteID;
        this.clienteRZ = clienteRZ;
        this.mtoTotal = mtoTotal;
        this.anulado = anulado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getSerieDocumento() {
        return serieDocumento;
    }

    public void setSerieDocumento(String serieDocumento) {
        this.serieDocumento = serieDocumento;
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
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

    public Double getMtoTotal() {
        return mtoTotal;
    }

    public void setMtoTotal(Double mtoTotal) {
        this.mtoTotal = mtoTotal;
    }

    public String getAnulado() {
        return anulado;
    }

    public void setAnulado(String anulado) {
        this.anulado = anulado;
    }
}
