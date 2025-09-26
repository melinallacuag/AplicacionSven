package com.anggastudio.sample.WebApiSVEN.Models;

public class ActualizarCredito {

    private String tipoDocumento;
    private String serieDocumento;
    private String nroDocumento;
    private String nroPlaca;
    private String observacionCA;

    public ActualizarCredito(String tipoDocumento, String serieDocumento, String nroDocumento, String nroPlaca, String observacionCA) {
        this.tipoDocumento = tipoDocumento;
        this.serieDocumento = serieDocumento;
        this.nroDocumento = nroDocumento;
        this.nroPlaca = nroPlaca;
        this.observacionCA = observacionCA;
    }
}
