package com.anggastudio.sample.WebApiSVEN.Models;

public class SettingTEgreso {
    private Integer companyID;
    private Integer egresoID;
    private String egresoDs;
    private Boolean status;

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public Integer getEgresoID() {
        return egresoID;
    }

    public void setEgresoID(Integer egresoID) {
        this.egresoID = egresoID;
    }

    public String getEgresoDs() {
        return egresoDs;
    }

    public void setEgresoDs(String egresoDs) {
        this.egresoDs = egresoDs;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
