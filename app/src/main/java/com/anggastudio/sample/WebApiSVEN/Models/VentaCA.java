package com.anggastudio.sample.WebApiSVEN.Models;

public class VentaCA {

    private Integer companyID;
    private String  tipoDocumento;
    private String  serieDocumento;
    private String  nroDocumento;
    private String  terminalID;
    private String  clienteID;
    private String  clienteRUC;
    private String  clienteRZ;
    private String  clienteDR;
    private Integer turno;
    private String  fechaProceso;
    private String  fechaDocumento;
    private String  fechaAtencion;
    private Double  mtoDescuento;
    private Double  mtoSubTotal;
    private Double  mtoImpuesto;
    private Double  mtoTotal;
    private String  nroPlaca;
    private String  odometro;
    private String  tipoVenta;
    private String  observacion;
    private String  referencia;
    private String  nroTarjetaNotaD;
    private String  nroTarjetaPuntos;
    private Double  puntosGanados;
    private Double  puntosDisponibles;
    private Double  montoCanjeado;
    private String  userID;
    private Integer nroItem;
    private String  articuloID;
    private String  articuloDS;
    private String  uniMed;
    private Integer almacenID;
    private Integer impuestoID;
    private Integer impuestoValor;
    private Double  precio1;
    private Double  precio2;
    private Double  cantidad;
    private Double  fise;
    private String  tranID;
    private String  nroLado;
    private String  manguera;
    private String  observacionDet;
    private Integer pagoID;
    private Integer tarjetaID;
    private String  tarjetaDS;
    private Double  mtoPagoPEN;
    private Double  mtoPagoUSD;
    private String  observacionPag;

    public VentaCA(Integer companyID, String tipoDocumento, String serieDocumento, String nroDocumento, String terminalID, String clienteID, String clienteRUC, String clienteRZ, String clienteDR, Integer turno, String fechaProceso, String fechaDocumento, String fechaAtencion, Double mtoDescuento, Double mtoSubTotal, Double mtoImpuesto, Double mtoTotal, String nroPlaca, String odometro, String tipoVenta, String observacion, String referencia, String nroTarjetaNotaD, String nroTarjetaPuntos, Double puntosGanados, Double puntosDisponibles, Double montoCanjeado, String userID, Integer nroItem, String articuloID, String articuloDS, String uniMed, Integer almacenID, Integer impuestoID, Integer impuestoValor, Double precio1, Double precio2, Double cantidad, Double fise, String tranID, String nroLado, String manguera, String observacionDet, Integer pagoID, Integer tarjetaID, String tarjetaDS, Double mtoPagoPEN, Double mtoPagoUSD, String observacionPag) {
        this.companyID = companyID;
        this.tipoDocumento = tipoDocumento;
        this.serieDocumento = serieDocumento;
        this.nroDocumento = nroDocumento;
        this.terminalID = terminalID;
        this.clienteID = clienteID;
        this.clienteRUC = clienteRUC;
        this.clienteRZ = clienteRZ;
        this.clienteDR = clienteDR;
        this.turno = turno;
        this.fechaProceso = fechaProceso;
        this.fechaDocumento = fechaDocumento;
        this.fechaAtencion = fechaAtencion;
        this.mtoDescuento = mtoDescuento;
        this.mtoSubTotal = mtoSubTotal;
        this.mtoImpuesto = mtoImpuesto;
        this.mtoTotal = mtoTotal;
        this.nroPlaca = nroPlaca;
        this.odometro = odometro;
        this.tipoVenta = tipoVenta;
        this.observacion = observacion;
        this.referencia = referencia;
        this.nroTarjetaNotaD = nroTarjetaNotaD;
        this.nroTarjetaPuntos = nroTarjetaPuntos;
        this.puntosGanados = puntosGanados;
        this.puntosDisponibles = puntosDisponibles;
        this.montoCanjeado = montoCanjeado;
        this.userID = userID;
        this.nroItem = nroItem;
        this.articuloID = articuloID;
        this.articuloDS = articuloDS;
        this.uniMed = uniMed;
        this.almacenID = almacenID;
        this.impuestoID = impuestoID;
        this.impuestoValor = impuestoValor;
        this.precio1 = precio1;
        this.precio2 = precio2;
        this.cantidad = cantidad;
        this.fise = fise;
        this.tranID = tranID;
        this.nroLado = nroLado;
        this.manguera = manguera;
        this.observacionDet = observacionDet;
        this.pagoID = pagoID;
        this.tarjetaID = tarjetaID;
        this.tarjetaDS = tarjetaDS;
        this.mtoPagoPEN = mtoPagoPEN;
        this.mtoPagoUSD = mtoPagoUSD;
        this.observacionPag = observacionPag;
    }

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getSerieDocumento() {
        return serieDocumento;
    }

    public void setSerieDocumento(String serieDocumento) {
        this.serieDocumento = serieDocumento;
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    public String getClienteID() {
        return clienteID;
    }

    public void setClienteID(String clienteID) {
        this.clienteID = clienteID;
    }

    public String getClienteRUC() {
        return clienteRUC;
    }

    public void setClienteRUC(String clienteRUC) {
        this.clienteRUC = clienteRUC;
    }

    public String getClienteRZ() {
        return clienteRZ;
    }

    public void setClienteRZ(String clienteRZ) {
        this.clienteRZ = clienteRZ;
    }

    public String getClienteDR() {
        return clienteDR;
    }

    public void setClienteDR(String clienteDR) {
        this.clienteDR = clienteDR;
    }

    public Integer getTurno() {
        return turno;
    }

    public void setTurno(Integer turno) {
        this.turno = turno;
    }

    public String getFechaProceso() {
        return fechaProceso;
    }

    public void setFechaProceso(String fechaProceso) {
        this.fechaProceso = fechaProceso;
    }

    public String getFechaDocumento() {
        return fechaDocumento;
    }

    public void setFechaDocumento(String fechaDocumento) {
        this.fechaDocumento = fechaDocumento;
    }

    public String getFechaAtencion() {
        return fechaAtencion;
    }

    public void setFechaAtencion(String fechaAtencion) {
        this.fechaAtencion = fechaAtencion;
    }

    public Double getMtoDescuento() {
        return mtoDescuento;
    }

    public void setMtoDescuento(Double mtoDescuento) {
        this.mtoDescuento = mtoDescuento;
    }

    public Double getMtoSubTotal() {
        return mtoSubTotal;
    }

    public void setMtoSubTotal(Double mtoSubTotal) {
        this.mtoSubTotal = mtoSubTotal;
    }

    public Double getMtoImpuesto() {
        return mtoImpuesto;
    }

    public void setMtoImpuesto(Double mtoImpuesto) {
        this.mtoImpuesto = mtoImpuesto;
    }

    public Double getMtoTotal() {
        return mtoTotal;
    }

    public void setMtoTotal(Double mtoTotal) {
        this.mtoTotal = mtoTotal;
    }

    public String getNroPlaca() {
        return nroPlaca;
    }

    public void setNroPlaca(String nroPlaca) {
        this.nroPlaca = nroPlaca;
    }

    public String getOdometro() {
        return odometro;
    }

    public void setOdometro(String odometro) {
        this.odometro = odometro;
    }

    public String getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(String tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getNroTarjetaNotaD() {
        return nroTarjetaNotaD;
    }

    public void setNroTarjetaNotaD(String nroTarjetaNotaD) {
        this.nroTarjetaNotaD = nroTarjetaNotaD;
    }

    public String getNroTarjetaPuntos() {
        return nroTarjetaPuntos;
    }

    public void setNroTarjetaPuntos(String nroTarjetaPuntos) {
        this.nroTarjetaPuntos = nroTarjetaPuntos;
    }

    public Double getPuntosGanados() {
        return puntosGanados;
    }

    public void setPuntosGanados(Double puntosGanados) {
        this.puntosGanados = puntosGanados;
    }

    public Double getPuntosDisponibles() {
        return puntosDisponibles;
    }

    public void setPuntosDisponibles(Double puntosDisponibles) {
        this.puntosDisponibles = puntosDisponibles;
    }

    public Double getMontoCanjeado() {
        return montoCanjeado;
    }

    public void setMontoCanjeado(Double montoCanjeado) {
        this.montoCanjeado = montoCanjeado;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Integer getNroItem() {
        return nroItem;
    }

    public void setNroItem(Integer nroItem) {
        this.nroItem = nroItem;
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

    public String getUniMed() {
        return uniMed;
    }

    public void setUniMed(String uniMed) {
        this.uniMed = uniMed;
    }

    public Integer getAlmacenID() {
        return almacenID;
    }

    public void setAlmacenID(Integer almacenID) {
        this.almacenID = almacenID;
    }

    public Integer getImpuestoID() {
        return impuestoID;
    }

    public void setImpuestoID(Integer impuestoID) {
        this.impuestoID = impuestoID;
    }

    public Integer getImpuestoValor() {
        return impuestoValor;
    }

    public void setImpuestoValor(Integer impuestoValor) {
        this.impuestoValor = impuestoValor;
    }

    public Double getPrecio1() {
        return precio1;
    }

    public void setPrecio1(Double precio1) {
        this.precio1 = precio1;
    }

    public Double getPrecio2() {
        return precio2;
    }

    public void setPrecio2(Double precio2) {
        this.precio2 = precio2;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public Double getFise() {
        return fise;
    }

    public void setFise(Double fise) {
        this.fise = fise;
    }

    public String getTranID() {
        return tranID;
    }

    public void setTranID(String tranID) {
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

    public String getObservacionDet() {
        return observacionDet;
    }

    public void setObservacionDet(String observacionDet) {
        this.observacionDet = observacionDet;
    }

    public Integer getPagoID() {
        return pagoID;
    }

    public void setPagoID(Integer pagoID) {
        this.pagoID = pagoID;
    }

    public Integer getTarjetaID() {
        return tarjetaID;
    }

    public void setTarjetaID(Integer tarjetaID) {
        this.tarjetaID = tarjetaID;
    }

    public String getTarjetaDS() {
        return tarjetaDS;
    }

    public void setTarjetaDS(String tarjetaDS) {
        this.tarjetaDS = tarjetaDS;
    }

    public Double getMtoPagoPEN() {
        return mtoPagoPEN;
    }

    public void setMtoPagoPEN(Double mtoPagoPEN) {
        this.mtoPagoPEN = mtoPagoPEN;
    }

    public Double getMtoPagoUSD() {
        return mtoPagoUSD;
    }

    public void setMtoPagoUSD(Double mtoPagoUSD) {
        this.mtoPagoUSD = mtoPagoUSD;
    }

    public String getObservacionPag() {
        return observacionPag;
    }

    public void setObservacionPag(String observacionPag) {
        this.observacionPag = observacionPag;
    }
}
