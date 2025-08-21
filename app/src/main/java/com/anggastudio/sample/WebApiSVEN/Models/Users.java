package com.anggastudio.sample.WebApiSVEN.Models;

import com.google.gson.annotations.SerializedName;

public class Users {

    private String userID;
    private String password;
    private String identFID;
    private String names;
    private String phone;
    private String mail;
    private Boolean locked;
    private Boolean cancel;
    @SerializedName("super")
    private Boolean isSuper;
    private Boolean forzarCierre;
    private Boolean afiliar;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdentFID() {
        return identFID;
    }

    public void setIdentFID(String identFID) {
        this.identFID = identFID;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Boolean getCancel() {
        return cancel;
    }

    public void setCancel(Boolean cancel) {
        this.cancel = cancel;
    }

    public Boolean getSuper() {
        return isSuper;
    }

    public void setSuper(Boolean isSuper) {
        this.isSuper = isSuper;
    }

    public Boolean getForzarCierre() {
        return forzarCierre;
    }

    public void setForzarCierre(Boolean forzarCierre) {
        this.forzarCierre = forzarCierre;
    }

    public Boolean getAfiliar() {
        return afiliar;
    }

    public void setAfiliar(Boolean afiliar) {
        this.afiliar = afiliar;
    }
}
