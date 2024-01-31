package com.anggastudio.sample.WebApiSVEN.Models;

public class Articulo {

    private String articuloID;
    private String articuloDS1;
    private String familiaID;
    private String familiaDS;
    private String uniMed;
    private String monedaID;
    private Double precio_Venta;
    private Double stock_Actual;
    private Double salidas;
    private String codigoBarra;
    private String imagen_Ruta;
    private Boolean generar_Vale;
    private Boolean usar_Decimales;
    private Boolean venta_Playa;
    private Boolean transfe_Gratuita;
    private Boolean bloqueo_Baja;
    private boolean seleccionado;

    public String getArticuloID() {
        return articuloID;
    }

    public void setArticuloID(String articuloID) {
        this.articuloID = articuloID;
    }

    public String getArticuloDS1() {
        return articuloDS1;
    }

    public void setArticuloDS1(String articuloDS1) {
        this.articuloDS1 = articuloDS1;
    }

    public String getFamiliaID() {
        return familiaID;
    }

    public void setFamiliaID(String familiaID) {
        this.familiaID = familiaID;
    }

    public String getFamiliaDS() {
        return familiaDS;
    }

    public void setFamiliaDS(String familiaDS) {
        this.familiaDS = familiaDS;
    }

    public String getUniMed() {
        return uniMed;
    }

    public void setUniMed(String uniMed) {
        this.uniMed = uniMed;
    }

    public String getMonedaID() {
        return monedaID;
    }

    public void setMonedaID(String monedaID) {
        this.monedaID = monedaID;
    }

    public Double getPrecio_Venta() {
        return precio_Venta;
    }

    public void setPrecio_Venta(Double precio_Venta) {
        this.precio_Venta = precio_Venta;
    }

    public Double getStock_Actual() {
        return stock_Actual;
    }

    public void setStock_Actual(Double stock_Actual) {
        this.stock_Actual = stock_Actual;
    }

    public Double getSalidas() {
        return salidas;
    }

    public void setSalidas(Double salidas) {
        this.salidas = salidas;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public String getImagen_Ruta() {
        return imagen_Ruta;
    }

    public void setImagen_Ruta(String imagen_Ruta) {
        this.imagen_Ruta = imagen_Ruta;
    }

    public Boolean getGenerar_Vale() {
        return generar_Vale;
    }

    public void setGenerar_Vale(Boolean generar_Vale) {
        this.generar_Vale = generar_Vale;
    }

    public Boolean getUsar_Decimales() {
        return usar_Decimales;
    }

    public void setUsar_Decimales(Boolean usar_Decimales) {
        this.usar_Decimales = usar_Decimales;
    }

    public Boolean getVenta_Playa() {
        return venta_Playa;
    }

    public void setVenta_Playa(Boolean venta_Playa) {
        this.venta_Playa = venta_Playa;
    }

    public Boolean getTransfe_Gratuita() {
        return transfe_Gratuita;
    }

    public void setTransfe_Gratuita(Boolean transfe_Gratuita) {
        this.transfe_Gratuita = transfe_Gratuita;
    }

    public Boolean getBloqueo_Baja() {
        return bloqueo_Baja;
    }

    public void setBloqueo_Baja(Boolean bloqueo_Baja) {
        this.bloqueo_Baja = bloqueo_Baja;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }
}
