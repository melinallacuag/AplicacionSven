package com.anggastudio.sample.WebApiSVEN.Models;

public class VentaMarketCA {

    private String tipoDocumento;
    private String serieDocumento;
    private String nroDocumento;
    private String terminalID;
    private String clienteID;
    private String clienteRUC;
    private String clienteRZ;
    private String clienteDR;
    private String fechaDocumento;
    private Double mtoSubTotal;
    private Double mtoImpuesto;
    private Double mtoTotal;
    private String nroPlaca;
    private String userID;
    private Integer pagoID;
    private Integer tarjetaID;
    private String tarjetaDS;
    private Double mtoPagoPEN;
    private Double mtoPagoUSD;
    private String observacionPag;

    public VentaMarketCA(String tipoDocumento, String serieDocumento, String nroDocumento, String terminalID, String clienteID, String clienteRUC, String clienteRZ, String clienteDR, String fechaDocumento, Double mtoSubTotal, Double mtoImpuesto, Double mtoTotal, String nroPlaca, String userID, Integer pagoID, Integer tarjetaID, String tarjetaDS, Double mtoPagoPEN, Double mtoPagoUSD, String observacionPag) {
        this.tipoDocumento = tipoDocumento;
        this.serieDocumento = serieDocumento;
        this.nroDocumento = nroDocumento;
        this.terminalID = terminalID;
        this.clienteID = clienteID;
        this.clienteRUC = clienteRUC;
        this.clienteRZ = clienteRZ;
        this.clienteDR = clienteDR;
        this.fechaDocumento = fechaDocumento;
        this.mtoSubTotal = mtoSubTotal;
        this.mtoImpuesto = mtoImpuesto;
        this.mtoTotal = mtoTotal;
        this.nroPlaca = nroPlaca;
        this.userID = userID;
        this.pagoID = pagoID;
        this.tarjetaID = tarjetaID;
        this.tarjetaDS = tarjetaDS;
        this.mtoPagoPEN = mtoPagoPEN;
        this.mtoPagoUSD = mtoPagoUSD;
        this.observacionPag = observacionPag;
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

    public String getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
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

    public String getFechaDocumento() {
        return fechaDocumento;
    }

    public void setFechaDocumento(String fechaDocumento) {
        this.fechaDocumento = fechaDocumento;
    }

    public Double getMtoSubTotal() {
        return mtoSubTotal;
    }

    public void setMtoSubTotal(Double mtoSubTotal) {
        this.mtoSubTotal = mtoSubTotal;
    }

    public Double getMtoImpuesto() {
        return mtoImpuesto;
    }

    public void setMtoImpuesto(Double mtoImpuesto) {
        this.mtoImpuesto = mtoImpuesto;
    }

    public Double getMtoTotal() {
        return mtoTotal;
    }

    public void setMtoTotal(Double mtoTotal) {
        this.mtoTotal = mtoTotal;
    }

    public String getNroPlaca() {
        return nroPlaca;
    }

    public void setNroPlaca(String nroPlaca) {
        this.nroPlaca = nroPlaca;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Integer getPagoID() {
        return pagoID;
    }

    public void setPagoID(Integer pagoID) {
        this.pagoID = pagoID;
    }

    public Integer getTarjetaID() {
        return tarjetaID;
    }

    public void setTarjetaID(Integer tarjetaID) {
        this.tarjetaID = tarjetaID;
    }

    public String getTarjetaDS() {
        return tarjetaDS;
    }

    public void setTarjetaDS(String tarjetaDS) {
        this.tarjetaDS = tarjetaDS;
    }

    public Double getMtoPagoPEN() {
        return mtoPagoPEN;
    }

    public void setMtoPagoPEN(Double mtoPagoPEN) {
        this.mtoPagoPEN = mtoPagoPEN;
    }

    public Double getMtoPagoUSD() {
        return mtoPagoUSD;
    }

    public void setMtoPagoUSD(Double mtoPagoUSD) {
        this.mtoPagoUSD = mtoPagoUSD;
    }

    public String getObservacionPag() {
        return observacionPag;
    }

    public void setObservacionPag(String observacionPag) {
        this.observacionPag = observacionPag;
    }
}
