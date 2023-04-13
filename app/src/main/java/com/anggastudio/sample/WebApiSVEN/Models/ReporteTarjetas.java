package com.anggastudio.sample.WebApiSVEN.Models;

public class ReporteTarjetas {

    private String nroReferencia;
    private String nroDocumento;
    private String tipo;
    private Double monto;

    public ReporteTarjetas(String nroReferencia, String nroDocumento, String tipo, Double monto) {
        this.nroReferencia = nroReferencia;
        this.nroDocumento = nroDocumento;
        this.tipo = tipo;
        this.monto = monto;
    }

    public String getNroReferencia() {
        return nroReferencia;
    }

    public void setNroReferencia(String nroReferencia) {
        this.nroReferencia = nroReferencia;
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }
}
