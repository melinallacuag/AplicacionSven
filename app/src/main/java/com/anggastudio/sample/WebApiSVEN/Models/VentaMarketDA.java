package com.anggastudio.sample.WebApiSVEN.Models;

public class VentaMarketDA {

    private String tipoDocumento;
    private String serieDocumento;
    private String nroDocumento;
    private Integer nroItem;
    private String articuloID;
    private String articuloDS;
    private String uniMed;
    private String terminalID;
    private String fechaDocumento;
    private Double precio1;
    private Double cantidad;
    private Double mtoSubTotal;
    private Double mtoImpuesto;
    private Double mtoTotal;

    public VentaMarketDA(String tipoDocumento, String serieDocumento, String nroDocumento, Integer nroItem, String articuloID, String articuloDS, String uniMed, String terminalID, String fechaDocumento, Double precio1, Double cantidad, Double mtoSubTotal, Double mtoImpuesto, Double mtoTotal) {
        this.tipoDocumento = tipoDocumento;
        this.serieDocumento = serieDocumento;
        this.nroDocumento = nroDocumento;
        this.nroItem = nroItem;
        this.articuloID = articuloID;
        this.articuloDS = articuloDS;
        this.uniMed = uniMed;
        this.terminalID = terminalID;
        this.fechaDocumento = fechaDocumento;
        this.precio1 = precio1;
        this.cantidad = cantidad;
        this.mtoSubTotal = mtoSubTotal;
        this.mtoImpuesto = mtoImpuesto;
        this.mtoTotal = mtoTotal;
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

    public Integer getNroItem() {
        return nroItem;
    }

    public void setNroItem(Integer nroItem) {
        this.nroItem = nroItem;
    }

    public String getArticuloID() {
        return articuloID;
    }

    public void setArticuloID(String articuloID) {
        this.articuloID = articuloID;
    }

    public String getArticuloDS() {
        return articuloDS;
    }

    public void setArticuloDS(String articuloDS) {
        this.articuloDS = articuloDS;
    }

    public String getUniMed() {
        return uniMed;
    }

    public void setUniMed(String uniMed) {
        this.uniMed = uniMed;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    public String getFechaDocumento() {
        return fechaDocumento;
    }

    public void setFechaDocumento(String fechaDocumento) {
        this.fechaDocumento = fechaDocumento;
    }

    public Double getPrecio1() {
        return precio1;
    }

    public void setPrecio1(Double precio1) {
        this.precio1 = precio1;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
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
}
