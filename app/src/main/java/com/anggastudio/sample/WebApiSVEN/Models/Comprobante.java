package com.anggastudio.sample.WebApiSVEN.Models;

public class Comprobante {
    private String tipoDocumento;
    private String serieDocumento;
    private String nroDocumento;
    private String nroPlaca;
    private String observacionCA;
    private Integer pagoID;
    private Integer tarjetaID;
    private String tarjetaDS;
    private Double mtoTotalEfectivo;
    private String observacionPA;
    private Double mtoPagoPEN;

    public Comprobante(String tipoDocumento, String serieDocumento, String nroDocumento, String nroPlaca, String observacionCA, Integer pagoID, Integer tarjetaID, String tarjetaDS, Double mtoTotalEfectivo, String observacionPA, Double mtoPagoPEN) {
        this.tipoDocumento = tipoDocumento;
        this.serieDocumento = serieDocumento;
        this.nroDocumento = nroDocumento;
        this.nroPlaca = nroPlaca;
        this.observacionCA = observacionCA;
        this.pagoID = pagoID;
        this.tarjetaID = tarjetaID;
        this.tarjetaDS = tarjetaDS;
        this.mtoTotalEfectivo = mtoTotalEfectivo;
        this.observacionPA = observacionPA;
        this.mtoPagoPEN = mtoPagoPEN;
    }
}
