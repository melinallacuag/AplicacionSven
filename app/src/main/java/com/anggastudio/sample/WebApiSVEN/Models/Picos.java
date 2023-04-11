package com.anggastudio.sample.WebApiSVEN.Models;

public class Picos {
    private String mangueraID;
    private String nroLado;
    private String posicion;
    private String articuloID;
    private String descripcion;
    private String protocolo;
    private Double valor;

    public Picos(String mangueraID, String nroLado, String posicion, String articuloID, String descripcion, String protocolo, Double valor) {
        this.mangueraID = mangueraID;
        this.nroLado = nroLado;
        this.posicion = posicion;
        this.articuloID = articuloID;
        this.descripcion = descripcion;
        this.protocolo = protocolo;
        this.valor = valor;
    }

    public String getMangueraID() {
        return mangueraID;
    }

    public void setMangueraID(String mangueraID) {
        this.mangueraID = mangueraID;
    }

    public String getNroLado() {
        return nroLado;
    }

    public void setNroLado(String nroLado) {
        this.nroLado = nroLado;
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public String getArticuloID() {
        return articuloID;
    }

    public void setArticuloID(String articuloID) {
        this.articuloID = articuloID;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

}