package com.anggastudio.sample.WebApiSVEN.Controllers;
import com.anggastudio.sample.WebApiSVEN.Models.Anular;
import com.anggastudio.sample.WebApiSVEN.Models.Articulo;
import com.anggastudio.sample.WebApiSVEN.Models.CDia;
import com.anggastudio.sample.WebApiSVEN.Models.CTurno;
import com.anggastudio.sample.WebApiSVEN.Models.ClienteCredito;
import com.anggastudio.sample.WebApiSVEN.Models.ClientePrecio;
import com.anggastudio.sample.WebApiSVEN.Models.Company;
import com.anggastudio.sample.WebApiSVEN.Models.Correlativo;
import com.anggastudio.sample.WebApiSVEN.Models.DetalleVenta;
import com.anggastudio.sample.WebApiSVEN.Models.Familia;
import com.anggastudio.sample.WebApiSVEN.Models.Gratuita;
import com.anggastudio.sample.WebApiSVEN.Models.LClientes;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.ListaComprobante;
import com.anggastudio.sample.WebApiSVEN.Models.Mangueras;
import com.anggastudio.sample.WebApiSVEN.Models.Optran;
import com.anggastudio.sample.WebApiSVEN.Models.Placa;
import com.anggastudio.sample.WebApiSVEN.Models.RAnulados;
import com.anggastudio.sample.WebApiSVEN.Models.Reimpresion;
import com.anggastudio.sample.WebApiSVEN.Models.ReporteTarjetas;
import com.anggastudio.sample.WebApiSVEN.Models.ReporteVendedor;
import com.anggastudio.sample.WebApiSVEN.Models.Setting;
import com.anggastudio.sample.WebApiSVEN.Models.SettingTask;
import com.anggastudio.sample.WebApiSVEN.Models.SettingTurno;
import com.anggastudio.sample.WebApiSVEN.Models.Terminal;
import com.anggastudio.sample.WebApiSVEN.Models.TipoPago;
import com.anggastudio.sample.WebApiSVEN.Models.Users;
import com.anggastudio.sample.WebApiSVEN.Models.VContometro;
import com.anggastudio.sample.WebApiSVEN.Models.VProducto;
import com.anggastudio.sample.WebApiSVEN.Models.VTipoPago;
import com.anggastudio.sample.WebApiSVEN.Models.VentaCA;
import com.anggastudio.sample.WebApiSVEN.Models.VentaMarketCA;
import com.anggastudio.sample.WebApiSVEN.Models.VentaMarketDA;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {

    /**
     * @Users
     * @Conseguir el identificador "userID" - "05070608"
     */
    @GET("api/users/listado")
    Call<List<Users>> getUsers();

    @GET("api/users/listado/{id}")
    Call<List<Users>> findUsers(@Path("id") String id);

    /**
     * @Company
     * @Conseguir el identificador "companyID"
     */
    @GET("api/company/listado")
    Call<List<Company>> getCompany();

    @GET("api/company/listado/{id}")
    Call<List<Company>> findCompany(@Path("id") Integer id);

    /**
     * @Terminal
     * @Conseguir el ifentificador "imei"
     */
    @GET("api/terminal/listado")
    Call<List<Terminal>> getTerminal();

    @GET("api/terminal/listado/{id}")
    Call<List<Terminal>> findTerminal(@Path("id") String id);

    /**
     * @Lados
     * @Conseguir el identificador "terminalID"
     */
    @GET("api/lados/listado")
    Call<List<Lados>> getLados();

    @GET("api/lados/listado/{id}")
    Call<List<Lados>> findLados(@Path("id") String id);

    /**
     * @Manguera
     * @Guardar_ListadoMangueras
     */
    @GET("api/picos/listado")
    Call<List<Mangueras>> getMangueras();

    @GET("api/picos/listado2/{id}")
    Call<List<Mangueras>> findManguerasByTerminal(@Path("id") String id);

    @POST("api/picos/guardar")
    Call<Mangueras> postMangueras(@Body Mangueras mangueras);

    /**
     * @Cliente RUC - DNI
     */
    @GET("api/cliente/listado")
    Call<List<LClientes>> getCliente();

    @GET("api/cliente/listado/{id}")
    Call<List<LClientes>> findCliente(@Path("id") String id);

    @GET("api/cliente/listado/DNI")
    Call<List<LClientes>> getClienteDNI();

    @GET("api/cliente/listado/DNI/{id}")
    Call<List<LClientes>> findClienteDNI(@Path("id") String id);

    @GET("api/cliente/listado/RUC")
    Call<List<LClientes>> getClienteRUC();

    @GET("api/cliente/listado/RUC/{id}")
    Call<List<LClientes>> findClienteRUC(@Path("id") String id);

    /**
     * @Detalle de la Venta
     */
    @GET("api/detalleventa/listado/{id}")
    Call<List<DetalleVenta>> findDetalleVenta(@Path("id") String id);

    /**
     * @VentaCA
     */
    @POST("api/ventaca/guardar")
    Call<VentaCA> postVentaCA(@Body VentaCA ventaCA);

    /**
     * @Optran
     */
    @GET("api/optran/listado/{id}")
    Call<List<Optran>> findOptran(@Path("id") String id);


    /**
     * Tipo de Pago - Spinner Tarjeta
     */
    @GET("api/card/listado")
    Call<List<TipoPago>> getTipoPago();

    @GET("api/card/listado/{id}")
    Call<List<TipoPago>> findTipoPago(@Path("id") Integer id);

    /**
     * Placa
     */
    @GET("api/placa/listado")
    Call<List<Placa>> getPlaca();

    @GET("api/placa/listado/{id}")
    Call<List<Placa>> findPlaca(@Path("id") String id);


    /**
     * Configuración
     */
    @GET("api/setting/listado")
    Call<List<Setting>> getSetting();

    @GET("api/setting/listado/{id}")
    Call<List<Setting>> findSetting(@Path("id") Integer id);


    /**
     * Ajustes
     */
    @GET("api/settingTask/listado")
    Call<List<SettingTask>> getSettingTask();

    @GET("api/settingTask/listado/{id}")
    Call<List<SettingTask>> findSettingTask(@Path("id") String id);


    /**
     * Correlativo
     */
    @GET("api/correlativo/listado")
    Call<List<Correlativo>> getCorrelativo();

    @GET("api/correlativo/listado/{imei}/{tipodoc}/{rfid}/{articuloid}/{tranId}")
    Call<List<Correlativo>> findCorrelativo(@Path("imei") String imei, @Path("tipodoc") String tipodoc, @Path("rfid") String rfid, @Path("articuloid") String articuloid, @Path("tranId") String tranId);

    @GET("api/correlativo/listadosinrfid/{imei}/{tipodoc}/{clienteid}/{articuloid}/{tranId}")
    Call<List<Correlativo>> findCorrelativosinrfid(@Path("imei") String imei, @Path("tipodoc") String tipodoc, @Path("clienteid") String clienteid, @Path("articuloid") String articuloid, @Path("tranId") String tranId);

    /**
     * Venta por Contometro
     */

    @GET("api/rcontom/listado")
    Call<List<VContometro>> getVContometro();

    @GET("api/rcontom/listado/{id}")
    Call<List<VContometro>> findVContometro(@Path("id") String id);


    /**
     * Venta por Producto
     */

    @GET("api/rproducto/listado")
    Call<List<VProducto>> getVProducto();

    @GET("api/rproducto/listado/{id}/{turno}")
    Call<List<VProducto>> findVProducto(@Path("id") String id,@Path("turno") Integer turno);


    /**
     * Venta por Pago
     */

    @GET("api/rpago/listado")
    Call<List<VTipoPago>> getVTipoPago();

    @GET("api/rpago/listado/{id}/{turno}")
    Call<List<VTipoPago>> findVTipoPago(@Path("id") String id,@Path("turno") Integer turno);

    /**
     * Reporte de Transferencia Gratuita
     */
    @GET("api/rgratuita/listado/{id}/{turno}")
    Call<List<Gratuita>> findRGratuita(@Path("id") String id, @Path("turno") Integer turno);

    /**
     * Incio de Día
     */
    @GET("api/cdia/listado/{terminalid}")
    Call<List<CDia>> findCDia(@Path("terminalid") String terminalid);

    @POST("api/cdia/iniciar/{terminalid}")
    Call<CDia> postCDia(@Path("terminalid") String terminalid);

    /**
     * Cambio de Turno
     */
    @POST("api/cturno/cerrar/{id}")
    Call<CTurno> postCTurno(@Path("id") String id);


    /**
     * Descuentos (Cliente Precio)
     */
    @GET("api/clienteprecio/listado/{rfid}/{companyid}")
    Call<List<ClientePrecio>> findDescuentos(@Path("rfid") String rfid, @Path("companyid") String companyid);

   // Call<List<Descuentos>> findDescuentos(@Path("rfid") String rfid, @Path("companyid") String companyid);
    /**
     * Consultar Venta
     */
    @GET("api/consultarventa/listado/{id}")
    Call<List<ListaComprobante>> findConsultarVenta(@Path("id") String id);


    /**
     * Anular
     */
    @POST("api/anular/anular/{tipodoc}/{seriedoc}/{nrodoc}/{anuladoid}/{terminalid}")
    Call<Anular> postAnular(@Path("tipodoc") String tipodoc, @Path("seriedoc") String seriedoc, @Path("nrodoc") String nrodoc, @Path("anuladoid") String anuladoid, @Path("terminalid") String terminalid);

    @GET("api/anular/listado/{tipodoc}/{seriedoc}/{nrodoc}/{anuladoid}/{terminalid}")
    Call<List<Anular>> findAnular(@Path("tipodoc") String tipodoc, @Path("seriedoc") String seriedoc, @Path("nrodoc") String nrodoc, @Path("anuladoid") String anuladoid, @Path("terminalid") String terminalid);


    /**
     * Reimprimir
     */
    @GET("api/reimpresion/listado/{tipodoc}/{seriedoc}/{nrodoc}")
    Call<List<Reimpresion>> findReimpresion(@Path("tipodoc") String tipodoc, @Path("seriedoc") String seriedoc, @Path("nrodoc") String nrodoc);


    /**
     * Reporte por Tarjeta
     */

    @GET("api/rtarjeta/listado/{id}/{turno}")
    Call<List<ReporteTarjetas>> findRTarjetas(@Path("id") String id, @Path("turno") Integer turno);

    /**
     * R. Anulados
     */

    @GET("api/ranulados/listado/{terminalId}/{turno}/{tipo}")
    Call<List<RAnulados>> findRAnulados(@Path("terminalId") String terminalId, @Path("turno") String turno, @Path("tipo") String tipo);

    /**
     * R. Vendedor
     */

    @GET("api/rvendedor/listado/{idterminal}/{turno}")
    Call<List<ReporteVendedor>> findRVendedor(@Path("idterminal") String idterminal, @Path("turno") String turno);

    /**
     * Cliente Credito
     */
    @GET("api/clientelineacredito/listado")
    Call<List<ClienteCredito>> getClienteCredito();

    /**
     * Setting Turno
     */
    @GET("api/settingturno/listado")
    Call<List<SettingTurno>> getSettingTurno();

    /**
     * Familia
     */
    @GET("api/familia/listado")
    Call<List<Familia>> getFamilia();

    /**
     * Articulo
     */
    @GET("api/articulo/listado")
    Call<List<Articulo>> getArticulo();

    /**
     * @VentaMarketDA
     */
    @POST("api/ventamarketda/listado")
    Call<VentaMarketDA> postVentaMarketDA(@Body VentaMarketDA ventaMarketDA);

    @GET("api/ventamarketda/listado/{tipodoc}/{seriedoc}/{nrodoc}/{nroItem}/{articuloID}/{articuloDS}/{uniMed}/{terminalID}/{fechaDocumento}/{precio1}/{cantidad}/{mtoSubTotal}/{mtoImpuesto}/{mtoTotal}")
        Call<List<VentaMarketDA>> getMarketDA(@Path("tipodoc") String tipodoc, @Path("seriedoc") String seriedoc, @Path("nrodoc") String nrodoc, @Path("nroItem") Integer nroItem, @Path("articuloID") String articuloID, @Path("articuloDS") String articuloDS, @Path("uniMed") String uniMed, @Path("terminalID") String terminalID, @Path("fechaDocumento") String fechaDocumento, @Path("precio1") Double precio1, @Path("cantidad") Double cantidad, @Path("mtoSubTotal") Double mtoSubTotal, @Path("mtoImpuesto") Double mtoImpuesto, @Path("mtoTotal") Double mtoTotal);

    /**
     * @VentaMarketCA
     */
    @POST("api/ventamarketca/listado")
    Call<VentaMarketCA> postVentaMarketCA(@Body VentaMarketCA ventaMarketCA);

    @GET("api/ventamarketca/listado/{tipodoc}/{seriedoc}/{nrodoc}/{terminalID}/{clienteID}/{clienteRUC}/{clienteRZ}/{clienteDR}/{fechaDocumento}/{mtoSubTotal}/{mtoImpuesto}/{mtoTotal}/{nroPlaca}/{userID}/{pagoID}/{tarjetaID}/{tarjetaDS}/{mtoPagoPEN}/{mtoPagoUSD}/{observacionPag}")
    Call<List<VentaMarketCA>> getMarketCA(@Path("tipodoc") String tipodoc, @Path("seriedoc") String seriedoc, @Path("nrodoc") String nrodoc, @Path("terminalID") String terminalID,@Path("clienteID") String clienteID, @Path("clienteRUC") String clienteRUC, @Path("clienteRZ") String clienteRZ, @Path("clienteDR") String clienteDR, @Path("fechaDocumento") String fechaDocumento,@Path("mtoSubTotal") Double mtoSubTotal,@Path("mtoImpuesto") Double mtoImpuesto,@Path("mtoTotal") Double mtoTotal, @Path("nroPlaca") String nroPlaca, @Path("userID") String userID, @Path("pagoID") Integer pagoID, @Path("tarjetaID") Integer tarjetaID,@Path("tarjetaDS") String tarjetaDS,@Path("mtoPagoPEN") Double mtoPagoPEN,@Path("mtoPagoUSD") Double mtoPagoUSD,@Path("observacionPag") String observacionPag);

    /**
     * @PGratuito
     */

    /**
     * Articulo
     */
    @GET("api/articulo/listadotg")
    Call<List<Articulo>> getArticuloG();
}
