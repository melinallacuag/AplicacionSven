package com.anggastudio.sample.WebApiSVEN.Models;

public class SettingVehiculo {

    private Integer companyID;
    private Integer vehiculoID;
    private String vehiculoDs;
    private Boolean vehiculoDf;

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public Integer getVehiculoID() {
        return vehiculoID;
    }

    public void setVehiculoID(Integer vehiculoID) {
        this.vehiculoID = vehiculoID;
    }

    public String getVehiculoDs() {
        return vehiculoDs;
    }

    public void setVehiculoDs(String vehiculoDs) {
        this.vehiculoDs = vehiculoDs;
    }

    public Boolean getVehiculoDf() {
        return vehiculoDf;
    }

    public void setVehiculoDf(Boolean vehiculoDf) {
        this.vehiculoDf = vehiculoDf;
    }
}
