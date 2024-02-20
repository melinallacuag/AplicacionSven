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
     */
    @GET("api/users/listado")
    Call<List<Users>> getUsers();

    @GET("api/users/listado/{userID}")
    Call<List<Users>> findUsers(@Path("userID") String userID);

    /**
     * @Company
     */
    @GET("api/company/listado")
    Call<List<Company>> getCompany();

    @GET("api/company/listado/{companyID}")
    Call<List<Company>> findCompany(@Path("companyID") Integer companyID);

    /**
     * @Terminal
     */
    @GET("api/terminal/listado")
    Call<List<Terminal>> getTerminal();

    @GET("api/terminal/listado/{imei}")
    Call<List<Terminal>> findTerminal(@Path("imei") String imei);

    /**
     * @Lados
     */
    @GET("api/lados/listado")
    Call<List<Lados>> getLados();

    @GET("api/lados/listado/{imei}")
    Call<List<Lados>> findLados(@Path("imei") String imei);

    @GET("api/lados/agregar/{nroLado}/{terminalID}")
    Call<List<Lados>> findLadosAgregar(@Path("nroLado") String nroLado,@Path("terminalID") String terminalID);

    @GET("api/lados/quitar/{nroLado}/{terminalID}")
    Call<List<Lados>> findLadosQuitar(@Path("nroLado") String nroLado,@Path("terminalID") String terminalID);

    /**
     * @Manguera
     */
    @GET("api/picos/listado")
    Call<List<Mangueras>> getMangueras();

    @GET("api/picos/listado2/{terminalID}")
    Call<List<Mangueras>> findManguerasByTerminal(@Path("terminalID") String terminalID);

    @POST("api/picos/guardar")
    Call<Mangueras> postMangueras(@Body Mangueras mangueras);

    /**
     * @ClienteRUC/DNI
     */
    @GET("api/cliente/listado")
    Call<List<LClientes>> getCliente();

    @GET("api/cliente/listado/{id}")
    Call<List<LClientes>> findCliente(@Path("id") String id);

    @GET("api/cliente/listado/DNI")
    Call<List<LClientes>> getClienteDNI();

    @GET("api/cliente/listado/DNI/{clienteId}")
    Call<List<LClientes>> findClienteDNI(@Path("clienteId") String clienteId);

    @GET("api/cliente/listado/RUC")
    Call<List<LClientes>> getClienteRUC();

    @GET("api/cliente/listado/RUC/{clienteId}")
    Call<List<LClientes>> findClienteRUC(@Path("clienteId") String clienteId);

    /**
     * @DetalleVenta
     */
    @GET("api/detalleventa/listado/{imei}")
    Call<List<DetalleVenta>> findDetalleVenta(@Path("imei") String imei);

    /**
     * @VentaCA
     */
    @POST("api/ventaca/guardar")
    Call<VentaCA> postVentaCA(@Body VentaCA ventaCA);

    /**
     * @Optran
     */
    @GET("api/optran/listado/{imei}")
    Call<List<Optran>> findOptran(@Path("imei") String imei);

    /**
     * @TipoPago
     */
    @GET("api/card/listado")
    Call<List<TipoPago>> getTipoPago();

    @GET("api/card/listado/{id}")
    Call<List<TipoPago>> findTipoPago(@Path("id") Integer id);

    /**
     * @Placa
     */
    @GET("api/placa/listado")
    Call<List<Placa>> getPlaca();

    @GET("api/placa/listado/{id}")
    Call<List<Placa>> findPlaca(@Path("id") String id);

    /**
     * @Setting
     */
    @GET("api/setting/listado")
    Call<List<Setting>> getSetting();

    @GET("api/setting/listado/{companyID}")
    Call<List<Setting>> findSetting(@Path("companyID") Integer companyID);

    /**
     * @SettingTask
     */
    @GET("api/settingTask/listado")
    Call<List<SettingTask>> getSettingTask();

    @GET("api/settingTask/listado/{id}")
    Call<List<SettingTask>> findSettingTask(@Path("id") String id);

    /**
     * @SettingTurno
     */
    @GET("api/settingturno/listado")
    Call<List<SettingTurno>> getSettingTurno();

    /**
     * @Correlativo
     */
    @GET("api/correlativo/listado")
    Call<List<Correlativo>> getCorrelativo();

    @GET("api/correlativo/listado/{imei}/{tipoDoc}/{rfid}/{articuloId}/{tranId}")
    Call<List<Correlativo>> findCorrelativo(@Path("imei") String imei, @Path("tipoDoc") String tipoDoc, @Path("rfid") String rfid, @Path("articuloId") String articuloId, @Path("tranId") String tranId);

    @GET("api/correlativo/listadosinrfid/{imei}/{tipoDoc}/{clienteId}/{articuloId}/{tranId}")
    Call<List<Correlativo>> findCorrelativosinrfid(@Path("imei") String imei, @Path("tipoDoc") String tipoDoc, @Path("clienteId") String clienteId, @Path("articuloId") String articuloId, @Path("tranId") String tranId);

    /**
     * @CIERREX:VentaContometro
     */
    @GET("api/rcontom/listado")
    Call<List<VContometro>> getVContometro();

    @GET("api/rcontom/listado/{terminalID}")
    Call<List<VContometro>> findVContometro(@Path("terminalID") String terminalID);

    /**
     * @CIERREX:VentaProducto
     */
    @GET("api/rproducto/listado")
    Call<List<VProducto>> getVProducto();

    @GET("api/rproducto/listado/{terminalID}/{terminalTurno}")
    Call<List<VProducto>> findVProducto(@Path("terminalID") String terminalID,@Path("terminalTurno") Integer terminalTurno);

    /**
     * @CIERREX:VentaPago
     */
    @GET("api/rpago/listado")
    Call<List<VTipoPago>> getVTipoPago();

    @GET("api/rpago/listado/{terminalID}/{terminalTurno}")
    Call<List<VTipoPago>> findVTipoPago(@Path("terminalID") String terminalID,@Path("terminalTurno") Integer terminalTurno);

    /**
     * @CIERREX:ReporteTransferenciaGratuita
     */
    @GET("api/rgratuita/listado/{terminalID}/{terminalTurno}")
    Call<List<Gratuita>> findRGratuita(@Path("terminalID") String terminalID, @Path("terminalTurno") Integer terminalTurno);

    /**
     * @CIERREX:ReporteTarjeta
     */
    @GET("api/rtarjeta/listado/{terminalID}/{terminalTurno}")
    Call<List<ReporteTarjetas>> findRTarjetas(@Path("terminalID") String terminalID, @Path("terminalTurno") Integer terminalTurno);

    /**
     * @CIERREX:ReporteAnulados
     */
    @GET("api/ranulados/listado/{terminalId}/{terminalTurno}/{tipo}")
    Call<List<RAnulados>> findRAnulados(@Path("terminalId") String terminalId, @Path("terminalTurno") String terminalTurno, @Path("tipo") String tipo);

    /**
     * @CIERREX:ReporteVendedor
     */
    @GET("api/rvendedor/listado/{terminalId}/{terminalTurno}")
    Call<List<ReporteVendedor>> findRVendedor(@Path("terminalId") String terminalId, @Path("terminalTurno") String terminalTurno);

    /**
     * @IncioDía
     */
    @GET("api/cdia/listado/{terminalID}")
    Call<List<CDia>> findCDia(@Path("terminalID") String terminalID);

    @POST("api/cdia/iniciar/{terminalID}")
    Call<CDia> postCDia(@Path("terminalID") String terminalID);

    /**
     * @CambioTurno
     */
    @POST("api/cturno/cerrar/{terminalID}")
    Call<CTurno> postCTurno(@Path("terminalID") String terminalID);

    /**
     * @Descuentos(ClientePrecio)
     */
    @GET("api/clienteprecio/listado/{rfid}/{companyId}")
    Call<List<ClientePrecio>> findDescuentos(@Path("rfid") String rfid, @Path("companyId") String companyId);

    /**
     * @ConsultarVenta
     */
    @GET("api/consultarventa/listado/{terminalID}")
    Call<List<ListaComprobante>> findConsultarVenta(@Path("terminalID") String terminalID);

    /**
     * @Anular
     */
    @POST("api/anular/anular/{tipodoc}/{seriedoc}/{nrodoc}/{anuladoid}/{terminalid}")
    Call<Anular> postAnular(@Path("tipodoc") String tipodoc, @Path("seriedoc") String seriedoc, @Path("nrodoc") String nrodoc, @Path("anuladoid") String anuladoid, @Path("terminalid") String terminalid);

    @GET("api/anular/listado/{tipodoc}/{seriedoc}/{nrodoc}/{anuladoid}/{terminalid}")
    Call<List<Anular>> findAnular(@Path("tipodoc") String tipodoc, @Path("seriedoc") String seriedoc, @Path("nrodoc") String nrodoc, @Path("anuladoid") String anuladoid, @Path("terminalid") String terminalid);

    /**
     * @Reimprimir
     */
    @GET("api/reimpresion/listado/{tipodoc}/{seriedoc}/{nrodoc}")
    Call<List<Reimpresion>> findReimpresion(@Path("tipodoc") String tipodoc, @Path("seriedoc") String seriedoc, @Path("nrodoc") String nrodoc);

    /**
     * @ClienteCredito
     */
    @GET("api/clientelineacredito/listado")
    Call<List<ClienteCredito>> getClienteCredito();

    /**
     * @Familia
     */
    @GET("api/familia/listado")
    Call<List<Familia>> getFamilia();

    /**
     * @Articulo
     */
    @GET("api/articulo/listado")
    Call<List<Articulo>> getArticulo();

    /**
     * @ArticuloGratuito
     */
    @GET("api/articulo/listadotg")
    Call<List<Articulo>> getArticuloG();

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

}
