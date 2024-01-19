package com.anggastudio.sample.WebApiSVEN.Models;

public class ProCategorias {

    private String categoria;
    private String nomcategoria;

    public ProCategorias(String nomcategoria) {
        this.nomcategoria = nomcategoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getNomcategoria() {
        return nomcategoria;
    }

    public void setNomcategoria(String nomcategoria) {
        this.nomcategoria = nomcategoria;
    }


}
