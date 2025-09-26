package com.anggastudio.sample.WebApiSVEN.Models;

public class EliminarComp {

    private String tipoDocumento;
    private String serieDocumento;
    private String nroDocumento;

    public EliminarComp(String tipoDocumento, String serieDocumento, String nroDocumento) {
        this.tipoDocumento = tipoDocumento;
        this.serieDocumento = serieDocumento;
        this.nroDocumento = nroDocumento;
    }
}
