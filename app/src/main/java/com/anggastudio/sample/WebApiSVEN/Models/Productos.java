package com.anggastudio.sample.WebApiSVEN.Models;

public class Productos {

    private Integer id;
    private String  nombre;
    private String  codigo;
    private Double  precio;
    private Integer cantidad;
    private String  categoria;
    private String  imagen;
    private boolean prodVendido;

    private boolean seleccionado;

    public Productos(String nombre,String codigo, Double precio, Integer cantidad, String categoria,String imagen, Integer id,boolean prodVendido) {
        this.nombre       = nombre;
        this.codigo       = codigo;
        this.precio       = precio;
        this.cantidad     = cantidad;
        this.imagen       = imagen;
        this.categoria    = categoria;
        this.seleccionado = false;
        this.id           = id;
        this.prodVendido  = prodVendido;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean getProdVendido() {
        return prodVendido;
    }

    public void setProdVendido(boolean prodVendido) {
        this.prodVendido = prodVendido;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

}
