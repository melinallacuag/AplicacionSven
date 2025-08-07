package com.anggastudio.sample.WebApiSVEN.Models;

public class SettingMoneda {

    private Integer companyID;
    private Integer monedaID;
    private String monedaDs;
    private Boolean status;

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public Integer getMonedaID() {
        return monedaID;
    }

    public void setMonedaID(Integer monedaID) {
        this.monedaID = monedaID;
    }

    public String getMonedaDs() {
        return monedaDs;
    }

    public void setMonedaDs(String monedaDs) {
        this.monedaDs = monedaDs;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
