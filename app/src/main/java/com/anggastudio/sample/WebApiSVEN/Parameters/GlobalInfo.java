package com.anggastudio.sample.WebApiSVEN.Parameters;

import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.sample.WebApiSVEN.Controllers.APIService;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.Mangueras;

import java.util.List;

public class GlobalInfo {

    /**
     * Variable de numero de copias impresión
     */
    public static Integer getNumeroVecesIMP10;

    /**
     * Datos Recycler
     */
    public  static List<Lados> getladosList10;
    public  static List<Mangueras> getmanguerasList10;

    /**
     * Datos de la Empresa
     */
    public static String getNameCompany10;
    public static String getRucCompany10;
    public static String getAddressCompany10;
    public static String getBranchCompany10;
    public static String getPhoneCompany10;
    public static String getMailCompany10;
    public static String getManagerCompany10;
    public static String getSloganCompany10;

    /**
     * Datos del Usuario
     */
    public static String getuserID10;
    public static String getuserName10;
    public static String getuserPass10;
    public static Boolean getuserLocked10;

    /**
     * Datos de los Lados y Mangueras
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
    public static Integer getterminalCompanyID10 = 1;
    public static Integer getterminalAlmacenID10;
    public static String  getterminalFechaHoraCierre10;

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
     * Datos de la Placa
     */
    public static String getNroPlaca10;
    public static String getplacaClienteID10;
    public static String getplacaClienteRZ10;
    public static String getplacaClienteDR10;



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
     * Datos Consulta Venta
     */

    public static String  getconsultaventaFecha10;
    public static String  getconsultaventaTipoDocumentoID10;
    public static String  getconsultaventaSerieDocumento10;
    public static String  getconsultaventaNroDocumento10;
    public static String  getconsultaventaClienteID10;
    public static String  getconsultaventaClienteRZ10;
    public static Double  getconsultaventaMtoTotal10;
    public static String  getconsultaventaAnulado10;



    /**
     * URL - APIService
     */
    public static final String BASE_URL = "http://192.168.1.227:8081/";

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

}
