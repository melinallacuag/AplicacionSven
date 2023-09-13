package com.anggastudio.sample.WebApiSVEN.Models;

import java.util.Date;

public class CDia {

    private String terminalID;
    private String fecha_Proceso;
    private String fecha_Cierre;

    public String getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    public String getFecha_Proceso() {
        return fecha_Proceso;
    }

    public void setFecha_Proceso(String fecha_Proceso) {
        this.fecha_Proceso = fecha_Proceso;
    }

    public String getFecha_Cierre() {
        return fecha_Cierre;
    }

    public void setFecha_Cierre(String fecha_Cierre) {
        this.fecha_Cierre = fecha_Cierre;
    }
}
