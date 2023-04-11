package com.anggastudio.sample.WebApiSVEN.Parameters;

import android.text.BoringLayout;
import android.widget.Button;

import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;

public class GlobalInfo {

    /**
     * Variable de numero de copias impresión
     */
    public static Integer getNumeroVecesIMP10;

    /**
     * Datos de la Compania
     */
    public static String getNameCompany10;
    public static String getRucCompany10;
    public static String getAddressCompany10;
    public static String getBranchCompany10;
    public static String getPhoneCompany10;
    public static String getMainCompany10;
    public static String getManagerCompany10;
    public static String getSloganCompany10;

    /**
     * Datos del User
     */
    public static String getuserID10;
    public static String getuserName10;
    public static String getuserPass10;

    /**
     * Datos de la Cara y Manguera
     */
    public static String getCara10;
    public static String getPistola10;

    /**
     * Datos de la Terminal
     */
    public static String  getterminalID10 = "";
    public static String  getterminalImei10;
    public static String  getterminalFecha10;
    public static Integer getterminalTurno10;
    public static Integer getterminalCompanyID10;
    public static Integer getterminalAlmacenID10;
    public static Boolean getterminalConsultaSunat10;
    public static Boolean getterminalVentaAutomatica10;
    public static Boolean getterminalVentaPlaya10;
    public static Boolean getterminalVentaTienda10;
    public static Boolean getterminalVentaCredito10;
    public static Boolean getterminalVentaTarjeta10;
    public static Boolean getterminalVentaGratuita10;
    public static Boolean getterminalVentaSerafin10;
    public static String  getterminalFacturaSerie10;
    public static String  getterminalFacturaNumero10;
    public static String  getterminalBoletaSerie10;
    public static String  getterminalBoletaNumero10;
    public static String  getterminalCreditoSerie10;
    public static String  getterminalCreditoNumero10;
    public static String  getterminalDebitoSerie10;
    public static String  getterminalDebitoNumero10;
    public static String  getterminalSerafinSerie10;
    public static String  getterminalSerafinNumero10;
    public static String  getterminalDespachoSerie10;
    public static String  getterminalDespachoNumero10;

    /**
     * Datos de la Placa
     */
    public static String getNroPlaca10;
    public static String getplacaClienteID10;
    public static String getplacaClienteRZ10;
    public static String getplacaClienteDR10;

    /**
     * Datos del Cliente RUC-DNI
     */
    public static String  getclienteId10;
    public static String  getclienteRUC10;
    public static String  getclienteRZ10;
    public static String  getclienteDR10;
    public static Boolean getclienteConsulta_Sunat10;
    public static Integer getclienteDiasCredito10;
    public static String  getclienteTipoCliente10;

    /**
     * Datos de la Setting
     */
    public static Integer getsettingCompanyId10;
    public static String  getsettingTituloApp10;
    public static String  getsettingFuelName10;
    public static String  getsettingFuelGrupoID10;
    public static Integer getsettingFuelLados10;
    public static Double  getsettingFuelMontoMinimo10;
    public static Integer getsettingImpuestoID110;
    public static Integer getsettingImpuestoValor110;
    public static Integer getsettingImpuestoID210;
    public static Integer getsettingImpuestoValor210;
    public static String  getsettingMonedaID10;
    public static String  getsettingMonedaValor10;
    public static String  getsettingClienteID10;
    public static String  getsettingClienteRZ10;
    public static String  getsettingNroPlaca10;
    public static Double  getsettingDNIMontoMinimo10;
    public static String  getsettingtimerAppVenta10;

    /**
     * Datos de la Setting Task
     */
    public static String  getsettingtaskID10;
    public static String  getsettingtaskName10;
    public static Boolean getsettingtaskIsTask10;

    /**
     * Datos de la Optran
     */
    public static Boolean getpase10 = false;

    public static Integer  getoptranTranID10;
    public static String   getoptranNroLado10;
    public static String   getoptranManguera10;
    public static String   getoptranFechaTran10;
    public static String   getoptranArticuloID10;
    public static String   getoptranProductoDs10;
    public static Double   getoptranPrecio10;
    public static Double   getoptranGalones10;
    public static Double   getoptranSoles10;
    public static String   getoptranOperador10;
    public static String   getoptranCliente10;
    public static String   getoptranUniMed10;

    /**
     * Datos de la VentaCA
     */

    public static String  getventaca10;

    /**
     * Datos de la Correlativo
     */
    public static Boolean getpase11 = false;

    public static String  getcorrelativoTerminalID;
    public static String  getcorrelativoImei;
    public static String  getcorrelativoFecha;
    public static Integer getcorrelativoTurno;
    public static String  getcorrelativoSerie;
    public static String  getcorrelativoNumero;

    /**
     * Datos de la Cliente Sellecion
     */
    public static String  getclClienteID10;
    public static String getclClienteRUC10;
    public static String  getclClienteRZ10;
    public static String  getclClienteDR10;


    /**
     * Datos de Venta por Contometro
     */

    public static String  getvcontomFechaProceso10;
    public static Integer getvcontomTurno10;
    public static String getvcontomnRoLado10;
    public static String  getvcontomManguera10;
    public static String  getvcontomArticuloID10;
    public static String  getvcontomArticuloDS10;
    public static Double  getvcontomContomInicial10;
    public static Double  getvcontomContomFinal10;
    public static Double  getvcontomGalones10;
    public static Double  getvcontomPrecio10;
    public static Double  getvcontomSoles10;

    public static Double getvcontomGalones101 = 0.00;


    /**
     * Datos del Descuento
     */

    public static Boolean getDescuentoPase = false;

    public static String  getdescuentoClienteID10;
    public static String  getdescuentoTipoID10;
    public static String  getdescuentoArticuloID10;
    public static Double  getdescuentoDescuento10;
    public static String  getdescuentoTipoDescuento10;
    public static String  getdescuentoTipoRango10;
    public static Double  getdescuentoRango110;
    public static Double  getdescuentoRango210;

    /**
     * URL - APIService
     */
    public static final String BASE_URL = "http://192.168.1.227:8081/";

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

}
