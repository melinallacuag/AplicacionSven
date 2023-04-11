package com.anggastudio.sample.WebApiSVEN.Models;

import java.math.BigInteger;

public class Optran {

    private Integer tranID;
    private String nroLado;
    private String manguera;
    private String fechaTran;
    private String articuloID;
    private String productoDs;
    private Double precio;
    private Double galones;
    private Double soles;
    private String operador;
    private String cliente;
    private String uniMed;

    public Integer getTranID() {
        return tranID;
    }

    public void setTranID(Integer tranID) {
        this.tranID = tranID;
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

    public String getFechaTran() {
        return fechaTran;
    }

    public void setFechaTran(String fechaTran) {
        this.fechaTran = fechaTran;
    }

    public String getArticuloID() {
        return articuloID;
    }

    public void setArticuloID(String articuloID) {
        this.articuloID = articuloID;
    }

    public String getProductoDs() {
        return productoDs;
    }

    public void setProductoDs(String productoDs) {
        this.productoDs = productoDs;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getGalones() {
        return galones;
    }

    public void setGalones(Double galones) {
        this.galones = galones;
    }

    public Double getSoles() {
        return soles;
    }

    public void setSoles(Double soles) {
        this.soles = soles;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getUniMed() {
        return uniMed;
    }

    public void setUniMed(String uniMed) {
        this.uniMed = uniMed;
    }
}
