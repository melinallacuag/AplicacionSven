package com.anggastudio.sample.WebApiSVEN.Models;

public class VContometro {

    private String fechaProceso;
    private Integer turno;
    private String nroLado;
    private String manguera;
    private String articuloID;
    private String articuloDS;
    private Double contomInicial;
    private Double contomFinal;
    private Double galones;
    private Double precio;
    private Double soles;


    public String getFechaProceso() {
        return fechaProceso;
    }

    public void setFechaProceso(String fechaProceso) {
        this.fechaProceso = fechaProceso;
    }

    public Integer getTurno() {
        return turno;
    }

    public void setTurno(Integer turno) {
        this.turno = turno;
    }

    public String getNroLado() {
        return nroLado;
    }

    public void setNroLado(String nroLado) {
        this.nroLado = nroLado;
    }

    public String getManguera() {
        return manguera;
    }

    public void setManguera(String manguera) {
        this.manguera = manguera;
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

    public Double getContomInicial() {
        return contomInicial;
    }

    public void setContomInicial(Double contomInicial) {
        this.contomInicial = contomInicial;
    }

    public Double getContomFinal() {
        return contomFinal;
    }

    public void setContomFinal(Double contomFinal) {
        this.contomFinal = contomFinal;
    }

    public Double getGalones() {
        return galones;
    }

    public void setGalones(Double galones) {
        this.galones = galones;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getSoles() {
        return soles;
    }

    public void setSoles(Double soles) {
        this.soles = soles;
    }
}
